# Hệ thống nhận diện món ăn và tính dinh dưỡng bằng AI

## Kiến trúc cốt lõi

- AI model **không tích hợp trực tiếp trong app**
- AI được deploy trên server bằng **Flask API**
- App Android gửi ảnh qua HTTP POST đến server
- Server chạy model AI và trả JSON prediction
- Room lưu local trên máy
- Firebase Authentication cho đăng nhập
- Cloud Firestore để đồng bộ dữ liệu
- Firebase Storage để lưu ảnh món ăn
- Database dinh dưỡng riêng để tra calories và macro
- AI chỉ nhận diện chung, ví dụ: `pho_bo`
- Sau đó app sẽ map nhãn → món chuẩn → tính khẩu phần → tính dinh dưỡng → lưu lịch sử

---

# 1. Bức tranh tổng thể dự án

## Luồng hệ thống

Image → Upload API → AI Server → JSON Prediction → Map món chuẩn → Nutrition DB → Tính toán → Lưu lịch sử → Dashboard

---

## Luồng chi tiết

### 1. Người dùng đăng nhập

Dùng Firebase Authentication:

- Email/password
- Google Sign-In

---

### 2. Người dùng tạo hồ sơ sức khỏe

Thông tin:

- tuổi
- giới tính
- chiều cao
- cân nặng
- mức độ vận động
- mục tiêu:
  - giảm cân
  - giữ cân
  - tăng cân

---

### 3. App tính target dinh dưỡng

Tính:

- calories/ngày
- protein/ngày
- carbs/ngày
- fat/ngày

---

### 4. Người dùng chụp ảnh món ăn

App thực hiện:

- capture image
- resize/compress
- upload lên AI server qua HTTP POST

Ví dụ API:

```http
POST /predict
Content-Type: multipart/form-data
```

---

### 5. Flask AI Server xử lý

Server:

- nhận ảnh
- preprocess
- chạy model AI
- trả JSON

Ví dụ response:

```json
{
  "label": "pho_bo",
  "confidence": 0.96
}
```

---

### 6. App map nhãn AI sang món chuẩn

Ví dụ:

`pho_bo` → `Phở bò`

---

### 7. Người dùng chọn khẩu phần

Ví dụ:

- nhỏ = 0.8
- vừa = 1.0
- lớn = 1.3

---

### 8. App tính dinh dưỡng

Tính:

- calories
- protein
- carbs
- fat
- fiber
- sodium

---

### 9. Người dùng thêm topping/phụ

Ví dụ:

- thêm trứng
- thêm quẩy
- thêm thịt
- thêm nước béo

---

### 10. App lưu lịch sử

Local:

- Room Database

Cloud:

- Firestore

---

### 11. Dashboard cập nhật

Hiển thị:

- calories đã nạp
- calories còn lại
- macro còn thiếu
- gợi ý món tiếp theo

---

# 2. Kiến trúc dữ liệu

Dự án chia thành 5 layer.

---

## A. Presentation Layer

Android:

- Activity
- Fragment
- ViewModel

---

## B. Network Layer

Giao tiếp AI server:

- Retrofit
- OkHttp

Nhiệm vụ:

- upload image
- nhận prediction

---

## C. Local Data Layer

Dùng Room lưu:

- user profile
- meal history
- AI predictions
- cached foods

---

## D. Cloud Layer

Dùng Firestore lưu:

- user profile
- meal logs
- summaries
- settings

---

## E. Nutrition Layer

Database riêng:

- món ăn chuẩn
- calories
- macros
- portions
- toppings
- label mapping

---

# 3. AI Server Flask

## Kiến trúc server

```text
Android App
    ↓ HTTP
Flask API
    ↓
Preprocessing
    ↓
AI Model
    ↓
JSON Response
```

---

## API thiết kế

### Predict

```http
POST /predict
```

Request:

multipart/form-data

Field:

```text
image
```

Response:

```json
{
  "label":"pho_bo",
  "confidence":0.96
}
```

---

## Health Check

```http
GET /health
```

Response:

```json
{
  "status":"ok"
}
```

---

# 4. Database dinh dưỡng

## food_items

Thông tin:

- id
- food_code
- food_name
- ai_label
- category
- default_serving_size
- base_calories
- base_protein
- base_carbs
- base_fat
- base_fiber
- base_sodium

Ví dụ:

Phở bò:

- food_code: FOOD_PHO_BO_001
- ai_label: pho_bo
- calories: 450

---

## food_aliases

Map nhiều label:

Ví dụ:

- pho
- phở
- pho_bo
- beef_pho

---

## portion_rules

Ví dụ:

- small = 0.8
- medium = 1.0
- large = 1.3

---

## addon_items

Ví dụ:

- trứng = +70 kcal
- quẩy = +150 kcal
- thịt = +80 kcal
- nước béo = +50 kcal

---

# 5. Room Local Database

## UserProfileEntity

Lưu:

- user info
- health profile
- targets

---

## MealLogEntity

Lưu:

- food name
- ai label
- confidence
- portion
- addons
- calories
- macros
- image path
- sync status

---

## AiPredictionEntity

Lưu:

- image
- label
- confidence
- topK

---

## DailySummaryEntity

Lưu:

- calories eaten
- calories target
- remaining

---

# 6. Firestore

## users

Thông tin user.

---

## users/{uid}/meal_logs

Lịch sử ăn.

---

## users/{uid}/ai_predictions

Prediction logs.

---

# 7. Firebase Storage

Lưu:

- avatar
- meal images
- prediction images

Cấu trúc:

```text
users/{uid}/profile/
users/{uid}/meals/
users/{uid}/predictions/
```

---

# 8. Công thức tính nutrition

## Base

```text
base × portionMultiplier
```

## Final

```text
final = base + addons
```

Ví dụ:

Phở bò:

```text
450 × 1.3 = 585
+ egg = 70
+ quẩy = 150
```

Output:

```text
805 kcal
```

---

# 9. Công nghệ sử dụng

## Mobile

- Android Java/Kotlin
- MVVM
- Room
- Retrofit
- Coroutines

## Cloud

- Firebase Authentication
- Firestore
- Storage

## AI Backend

- Python
- Flask API
- TensorFlow/PyTorch

---

# 10. Điểm nổi bật

- Không cần nhúng model vào app
- App nhẹ hơn
- Dễ update model trên server
- Có thể nâng cấp AI mà không cần update app
- Có Room offline
- Có cloud sync
- Có nutrition database riêng
- Có khẩu phần và topping

---

# 11. Lộ trình triển khai

## Phase 1

- Room
- UI
- Nutrition DB

## Phase 2

- Retrofit
- Flask API

## Phase 3

- Firebase Auth
- Firestore
- Storage

## Phase 4

- Dashboard
- Analytics

## Phase 5

- Sync
- Optimization

---

# 12. Kết luận

Kiến trúc phù hợp nhất:

- AI chạy trên server Flask API
- Android app chỉ gửi ảnh và nhận kết quả
- Room là local database
- Firebase cho cloud
- Nutrition DB riêng để tra calories
- 1 món chuẩn cho mỗi nhãn AI
- khẩu phần + topping để tăng độ chính xác