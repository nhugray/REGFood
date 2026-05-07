from __future__ import annotations

import io
import json
import logging
import os
from dataclasses import dataclass
from functools import lru_cache
from typing import Any, Dict, List, Optional, Tuple

import numpy as np
from flask import Flask, jsonify, request
from flask_cors import CORS
from PIL import Image, ImageOps, UnidentifiedImageError
from tensorflow import keras


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
MODEL_PATH = os.path.join(BASE_DIR, "best_food_model.keras")
LABELS_PATH = os.path.join(BASE_DIR, "best_food_model_labels.json")

DEFAULT_CONFIDENCE_THRESHOLD = float(os.getenv("CONFIDENCE_THRESHOLD", "0.50"))
DEFAULT_TOP_K = int(os.getenv("TOP_K", "5"))
MAX_UPLOAD_MB = int(os.getenv("MAX_UPLOAD_MB", "10"))
MAX_IMAGE_BYTES = MAX_UPLOAD_MB * 1024 * 1024
ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "webp"}

app = Flask(__name__)
CORS(app)

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s | %(levelname)s | %(message)s",
)
logger = logging.getLogger("food-recognition-api")


@dataclass(frozen=True)
class ModelMetadata:
    class_names: List[str]
    image_size: int


@dataclass(frozen=True)
class PredictionItem:
    label: str
    score: float


@dataclass(frozen=True)
class PredictionResult:
    food: str
    confidence: float
    low_confidence: bool
    all_scores: List[Dict[str, Any]]
    suggestions: List[Dict[str, Any]]

    def to_dict(self) -> Dict[str, Any]:
        return {
            "food": self.food,
            "confidence": round(self.confidence, 4),
            "low_confidence": self.low_confidence,
            "all_scores": self.all_scores,
            "foodId": self.food,
            "name": self.food.replace("_", " ").title(),
            "description": (
                f"Đây có thể là món {self.food.replace('_', ' ')} "
                f"với độ tin cậy {round(self.confidence * 100, 1)}%."
            ),
            "imageUrl": None,
            "lowConfidence": self.low_confidence,
            "suggestions": self.suggestions,
        }


@lru_cache(maxsize=1)
def load_metadata() -> ModelMetadata:
    if not os.path.exists(LABELS_PATH):
        raise FileNotFoundError(f"Không tìm thấy labels: {LABELS_PATH}")

    with open(LABELS_PATH, "r", encoding="utf-8") as f:
        meta = json.load(f)

    class_names = list(meta.get("class_names", []))
    image_size = int(meta.get("image_size", 224))

    if not class_names:
        raise ValueError("Danh sách class_names rỗng")

    return ModelMetadata(class_names=class_names, image_size=image_size)


@lru_cache(maxsize=1)
def load_model_bundle() -> Tuple[Any, ModelMetadata]:
    metadata = load_metadata()
    if not os.path.exists(MODEL_PATH):
        raise FileNotFoundError(f"Không tìm thấy model: {MODEL_PATH}")

    model = keras.models.load_model(MODEL_PATH, compile=False)
    logger.info("Loaded model=%s | classes=%d | image_size=%d", MODEL_PATH, len(metadata.class_names), metadata.image_size)
    return model, metadata


def allowed_filename(filename: str) -> bool:
    return "." in filename and filename.rsplit(".", 1)[1].lower() in ALLOWED_EXTENSIONS


def preprocess_image(image_bytes: bytes, image_size: int) -> np.ndarray:
    if len(image_bytes) > MAX_IMAGE_BYTES:
        raise ValueError(f"File quá lớn. Tối đa {MAX_UPLOAD_MB} MB")

    try:
        image = Image.open(io.BytesIO(image_bytes))
        image = ImageOps.exif_transpose(image).convert("RGB")
    except UnidentifiedImageError as exc:
        raise ValueError("File không phải ảnh hợp lệ") from exc

    # Giữ đúng tỉ lệ ảnh khi resize để hạn chế méo hình làm model đoán lệch.
    image = ImageOps.fit(image, (image_size, image_size), method=Image.Resampling.BICUBIC)
    array = np.asarray(image, dtype=np.float32)
    array = np.expand_dims(array, axis=0)
    return array


def predict_top_k(image_bytes: bytes, top_k: int = DEFAULT_TOP_K) -> PredictionResult:
    model, metadata = load_model_bundle()
    x = preprocess_image(image_bytes, metadata.image_size)
    probs = np.asarray(model.predict(x, verbose=0)[0], dtype=np.float32)

    if probs.ndim != 1:
        probs = probs.reshape(-1)

    if len(probs) != len(metadata.class_names):
        raise ValueError(
            f"Số output của model ({len(probs)}) không khớp với labels ({len(metadata.class_names)})"
        )

    # Chuẩn hoá nhẹ để giảm trường hợp model trả về logits hoặc phân phối chưa chuẩn.
    probs_sum = float(np.sum(probs))
    if probs_sum > 0 and not np.isclose(probs_sum, 1.0, atol=0.1):
        probs = probs / probs_sum

    top_k = max(1, min(top_k, len(metadata.class_names)))
    indices = np.argsort(probs)[::-1][:top_k]

    items = [
        PredictionItem(label=metadata.class_names[i], score=float(probs[i]))
        for i in indices
    ]

    top_item = items[0]
    all_scores = [
        {"label": item.label, "score": round(item.score, 4)}
        for item in items
    ]
    suggestions = [
        {
            "foodId": item.label,
            "name": item.label.replace("_", " ").title(),
            "confidence": round(item.score, 4),
        }
        for item in items[1:]
    ]

    return PredictionResult(
        food=top_item.label,
        confidence=top_item.score,
        low_confidence=top_item.score < DEFAULT_CONFIDENCE_THRESHOLD,
        all_scores=all_scores,
        suggestions=suggestions,
    )


@app.get("/")
def health() -> Any:
    model_loaded = False
    classes = 0
    image_size = None
    try:
        _, metadata = load_model_bundle()
        model_loaded = True
        classes = len(metadata.class_names)
        image_size = metadata.image_size
    except Exception:
        pass

    return jsonify(
        {
            "status": "ok",
            "model_loaded": model_loaded,
            "num_classes": classes,
            "image_size": image_size,
            "endpoints": {
                "health": "GET /",
                "predict": "POST /api/recognize",
                "predict_v2": "POST /api/predict",
            },
        }
    )


@app.post("/api/recognize")
def recognize() -> Any:
    if "image" not in request.files:
        return jsonify({"success": False, "error": "Thiếu file ảnh với field 'image'"}), 400

    file = request.files["image"]
    if not file.filename:
        return jsonify({"success": False, "error": "Tên file không hợp lệ"}), 400
    if not allowed_filename(file.filename):
        return jsonify(
            {
                "success": False,
                "error": "Định dạng không hỗ trợ. Chỉ chấp nhận jpg, jpeg, png, webp",
            }
        ), 400

    try:
        result = predict_top_k(file.read(), top_k=DEFAULT_TOP_K)
        return jsonify({"success": True, "data": result.to_dict()})
    except ValueError as exc:
        return jsonify({"success": False, "error": str(exc)}), 400
    except Exception as exc:
        logger.exception("Prediction failed")
        return jsonify({"success": False, "error": f"Lỗi server: {exc}"}), 500


@app.post("/api/predict")
def predict() -> Any:
    """Alias tương thích cho app Android cũ/new."""
    return recognize()


@app.post("/api/recognize_base64")
def recognize_base64() -> Any:
    payload = request.get_json(silent=True) or {}
    image_base64 = payload.get("image_base64")
    if not image_base64:
        return jsonify({"success": False, "error": "Thiếu image_base64"}), 400

    import base64

    try:
        image_bytes = base64.b64decode(image_base64)
        result = predict_top_k(image_bytes, top_k=DEFAULT_TOP_K)
        return jsonify({"success": True, "data": result.to_dict()})
    except ValueError as exc:
        return jsonify({"success": False, "error": str(exc)}), 400
    except Exception as exc:
        logger.exception("Base64 prediction failed")
        return jsonify({"success": False, "error": f"Lỗi server: {exc}"}), 500


@app.errorhandler(404)
def not_found(_: Any) -> Any:
    return jsonify({"success": False, "error": "Endpoint không tồn tại"}), 404


@app.errorhandler(500)
def internal_error(_: Any) -> Any:
    return jsonify({"success": False, "error": "Lỗi nội bộ server"}), 500


if __name__ == "__main__":
    load_model_bundle()
    host = os.getenv("HOST", "0.0.0.0")
    port = int(os.getenv("PORT", "5000"))
    debug = os.getenv("FLASK_DEBUG", "0") == "1"
    app.run(host=host, port=port, debug=debug, threaded=True)
