# Food Recognition Flask API

API Flask này dùng model `best_food_model.keras` để nhận ảnh từ app Android, chạy nhận diện món ăn và trả về JSON kết quả.

## Tính năng

- Upload ảnh từ camera hoặc thư viện qua `multipart/form-data`
- API trả về kết quả top-k chuyên nghiệp, phù hợp tích hợp app
- Hỗ trợ `base64` nếu bạn muốn gửi ảnh đã mã hoá từ app
- CORS bật sẵn để dễ test từ nhiều client

## Cấu trúc file

- `best_food_model.keras`: model đã train
- `best_food_model_labels.json`: danh sách nhãn và kích thước ảnh đầu vào
- `server.py`: Flask API chính
- `requirements.txt`: thư viện cần cài

## Cài đặt

### Cách khuyên dùng: tạo virtual environment

Trên Windows/MSYS2, đừng cài trực tiếp vào Python hệ thống vì sẽ gặp lỗi `externally-managed-environment`.

```bash
python -m venv .venv
.\.venv\Scripts\activate
python -m pip install --upgrade pip
pip install -r requirements.txt
```

### Nếu bạn dùng MSYS2 UCRT64

Nếu đang mở `python` từ `C:\msys64\ucrt64\bin\python.exe`, hãy ưu tiên một trong hai cách sau:

1. Dùng môi trường ảo như trên
2. Hoặc cài gói Python tương ứng bằng `pacman`

Ví dụ gói thường cần có:

```bash
pacman -S mingw-w64-ucrt-x86_64-python mingw-w64-ucrt-x86_64-python-pip mingw-w64-ucrt-x86_64-python-numpy mingw-w64-ucrt-x86_64-python-pillow
```

Nếu `tensorflow` không có gói phù hợp trong MSYS2, cách ổn nhất là dùng Python từ `python.org` rồi tạo `venv` và cài bằng `pip`.

## Chạy server

```bash
python server.py
```

Mặc định server chạy tại:

```bash
http://0.0.0.0:5000
```

## Endpoint

### 1. Health check

```http
GET /
```

Response ví dụ:

```json
{
  "status": "ok",
  "model_loaded": true,
  "num_classes": 30,
  "image_size": 224
}
```

### 2. Nhận diện từ ảnh upload

```http
POST /api/recognize
```

Form-data:

- `image`: file ảnh

Response ví dụ:

```json
{
  "success": true,
  "data": {
    "food": "Pho",
    "confidence": 0.9321,
    "low_confidence": false,
    "all_scores": [
      {"label": "Pho", "score": 0.9321}
    ],
    "suggestions": []
  }
}
```

### 3. Alias tương thích

```http
POST /api/predict
```

Dùng cùng logic với `/api/recognize`.

### 4. Nhận diện từ base64

```http
POST /api/recognize_base64
```

Body JSON:

```json
{
  "image_base64": "..."
}
```

## Gợi ý tích hợp Android

- Nếu app của bạn chụp ảnh từ camera hoặc lấy từ thư viện, hãy upload file ảnh thẳng lên `/api/recognize`
- Nếu muốn gọn hơn, có thể nén ảnh trước khi gửi để giảm latency
- Nên resize ảnh ở phía client hoặc để server xử lý, nhưng không nên gửi ảnh quá lớn

## Biến môi trường

- `HOST`: mặc định `0.0.0.0`
- `PORT`: mặc định `5000`
- `FLASK_DEBUG`: `1` để bật debug
- `TOP_K`: số kết quả trả về, mặc định `5`
- `CONFIDENCE_THRESHOLD`: ngưỡng tin cậy, mặc định `0.50`
- `MAX_UPLOAD_MB`: giới hạn file upload, mặc định `10`

## Lưu ý triển khai

- Model TensorFlow khá nặng, nên chạy trên máy có đủ RAM/CPU
- Nếu deploy production, nên đặt sau Nginx/Gunicorn
- Nếu muốn app Android gọi qua mạng LAN, nhớ mở firewall và dùng IP máy chạy server
