# REGFood

REGFood là ứng dụng Android hỗ trợ nhận diện món ăn Việt Nam bằng AI, ước tính năng lượng dinh dưỡng và theo dõi lịch sử bữa ăn. Ứng dụng gửi ảnh món ăn đến Flask API chạy model AI, nhận kết quả dự đoán, cho phép người dùng xác nhận khẩu phần/topping, sau đó lưu dữ liệu vào Room Database và hiển thị lại trên các màn hình nhật ký, mục tiêu và thống kê.

## Tính năng chính

- Nhận diện món ăn từ ảnh chụp bằng camera hoặc ảnh chọn từ thư viện.
- Gửi ảnh đến AI server qua Retrofit/OkHttp với `multipart/form-data`.
- Hiển thị kết quả dự đoán, độ tin cậy và danh sách gợi ý khi model chưa chắc chắn.
- Xác nhận món ăn, khẩu phần, loại bữa ăn và topping trước khi lưu.
- Lưu lịch sử bữa ăn, ảnh món ăn và dữ liệu calories vào Room Database.
- Tra cứu danh mục món ăn và thông tin dinh dưỡng từ dữ liệu local.
- Đăng nhập bằng Firebase Authentication, gồm email/password và Google Sign-In.
- Lưu hồ sơ sức khỏe và mục tiêu dinh dưỡng; đồng bộ hồ sơ lên Cloud Firestore.
- Xem nhật ký bữa ăn, món yêu thích, tiến độ calories trong ngày và thống kê 7 ngày gần nhất.

## Công nghệ sử dụng

### Android

- Java
- AndroidX AppCompat, Activity, Fragment
- Material Components
- ConstraintLayout
- Lifecycle ViewModel và LiveData
- Room Database
- Retrofit, OkHttp, Gson
- Firebase Authentication
- Cloud Firestore
- Firebase Storage

### AI Backend

- Python
- Flask
- TensorFlow/Keras
- Pillow, NumPy

## Kiến trúc tổng quan

Ứng dụng dùng mô hình single-activity với `MainActivity` làm host cho các Fragment qua bottom navigation. Một số luồng đã đi theo MVVM rõ ràng, đặc biệt là luồng nhận diện món ăn:

```text
Fragment -> ViewModel -> Repository -> Retrofit API -> Flask AI Server
```

Các màn hình dữ liệu như Journal, Goals, Foods và Insights dùng repository để đọc/ghi Room Database hoặc Firebase:

```text
Fragment -> Repository -> Room / Firebase
```

Luồng nhận diện món ăn:

```text
Camera/Gallery
  -> cache image file
  -> POST /api/recognize
  -> AI prediction JSON
  -> confirm food/portion/topping
  -> save MealLogEntity
  -> update Journal/Home/Insights
```

## Cấu trúc thư mục

```text
REGFood/
├── app/
│   ├── src/main/java/com/finalterm/regfood/
│   │   ├── MainActivity.java
│   │   ├── app/navigation/             # Bottom tab model
│   │   ├── features/
│   │   │   ├── auth/                   # Firebase authentication
│   │   │   ├── foodrecognition/        # AI recognition, API, UI, meal sync
│   │   │   ├── foods/                  # Food catalog
│   │   │   ├── goals/                  # Health profile and nutrition targets
│   │   │   ├── home/                   # Home dashboard
│   │   │   ├── insights/               # Weekly analytics
│   │   │   └── journal/                # Meal journal
│   │   ├── local/                      # Room database, DAO, entity, repository
│   │   ├── server/                     # Flask AI API
│   │   └── shared/                     # Shared session and UI utilities
│   └── src/main/res/                   # XML layouts, drawables, values
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Yêu cầu môi trường

- Android Studio bản mới có hỗ trợ Android Gradle Plugin 8.x.
- JDK 17 hoặc JDK tương thích với Android Studio hiện tại.
- Thiết bị Android hoặc emulator từ Android 7.0 trở lên (`minSdk 24`).
- Python 3.10+ để chạy Flask AI server.
- Firebase project đã cấu hình Authentication, Firestore và Storage.

## Cài đặt và chạy Android app

1. Clone hoặc mở project trong Android Studio.

2. Kiểm tra file Firebase config:

   ```text
   app/google-services.json
   ```

   File này cần khớp với Firebase project đang dùng cho ứng dụng.

3. Cập nhật địa chỉ AI server trong:

   ```text
   app/src/main/java/com/finalterm/regfood/features/foodrecognition/data/api/ServerConfig.java
   ```

   Ví dụ khi dùng thiết bị thật cùng mạng LAN:

   ```java
   public static final String BASE_URL = "http://192.168.1.8:5000/";
   ```

   Nếu chạy trên emulator Android Studio và server chạy trên cùng máy, thường có thể dùng:

   ```java
   public static final String BASE_URL = "http://10.0.2.2:5000/";
   ```

4. Build project:

   ```powershell
   .\gradlew.bat assembleDebug
   ```

5. Chạy ứng dụng từ Android Studio hoặc cài APK debug lên thiết bị.

## Chạy Flask AI server

Thư mục server nằm tại:

```text
app/src/main/java/com/finalterm/regfood/server/
```

Các file chính:

- `server.py`: Flask API.
- `requirements.txt`: danh sách thư viện Python.
- `best_food_model.keras`: model nhận diện món ăn.
- `best_food_model_labels.json`: metadata nhãn và kích thước ảnh.

Cài đặt môi trường:

```powershell
cd app\src\main\java\com\finalterm\regfood\server
python -m venv .venv
.\.venv\Scripts\activate
python -m pip install --upgrade pip
pip install -r requirements.txt
```

Chạy server:

```powershell
python server.py
```

Mặc định server chạy tại:

```text
http://0.0.0.0:5000
```

Endpoint chính:

```http
GET /
POST /api/recognize
POST /api/predict
POST /api/recognize_base64
```

Với app Android, endpoint đang dùng là:

```http
POST /api/recognize
```

Form-data:

```text
image=<file ảnh>
```

Response rút gọn:

```json
{
  "success": true,
  "data": {
    "food": "pho",
    "confidence": 0.9321,
    "low_confidence": false,
    "all_scores": [],
    "suggestions": []
  }
}
```

## Dữ liệu local

Room Database sử dụng tên:

```text
regfood_local_db
```

Các bảng chính:

- `user_profiles`: hồ sơ sức khỏe và mục tiêu dinh dưỡng.
- `food_items`: danh mục món ăn và dữ liệu dinh dưỡng cơ bản.
- `food_aliases`: ánh xạ tên/nhãn phụ về món chuẩn.
- `portion_rules`: quy tắc khẩu phần.
- `meal_logs`: lịch sử bữa ăn.
- `ai_predictions`: dữ liệu dự đoán AI.
- `favorite_foods`: món ăn yêu thích.
- `daily_summaries`, `user_settings`, `addon_items`, `nutrition_reference`: dữ liệu hỗ trợ.

## Firebase

Ứng dụng đang dùng Firebase cho:

- Authentication: đăng nhập email/password và Google Sign-In.
- Firestore: lưu hồ sơ người dùng qua `UserProfileRepository`.
- Storage và Firestore cho đồng bộ meal logs đã có lớp triển khai (`MealSyncManager`, `FirebaseMealRemoteDataSource`), nhưng luồng xác nhận món hiện tại chủ yếu lưu bữa ăn vào Room Database.

## Kiểm thử

Dự án đã có local unit test cho các logic nghiệp vụ chính:

- Chuẩn hoá tên món, tra calories gốc, hệ số khẩu phần và calories topping.
- Tính calories mục tiêu, macro, hệ số vận động và điều chỉnh theo mục tiêu.
- Model kết quả đăng nhập.
- Model kết quả đồng bộ bữa ăn.

Dự án cũng có instrumented test chạy trên emulator/thiết bị cho Room DAO:

- Insert/query món ăn theo AI label và alias.
- Lọc danh sách món active.
- Insert/query lịch sử bữa ăn theo user và đánh dấu bữa đã sync.
- Thêm/tìm/xoá món yêu thích.
- Lưu và tìm hồ sơ người dùng theo Firebase UID.

Chạy unit test:

```powershell
.\gradlew.bat test
```

Nếu máy đang trỏ PATH vào Java 8, cần chạy bằng JDK 11+ trước khi gọi Gradle. Ví dụ:

```powershell
$env:JAVA_HOME='E:\EclipseTemurin'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat test
```

Kết quả kiểm thử gần nhất:

```text
BUILD SUCCESSFUL
```

Chạy instrumented test khi có emulator hoặc thiết bị:

```powershell
.\gradlew.bat connectedAndroidTest
```

Kết quả instrumented test gần nhất:

```text
tests="5" failures="0" errors="0" skipped="0"
```

## Ghi chú phát triển

- AI model không được nhúng trực tiếp vào app; app chỉ gửi ảnh và nhận JSON từ server.
- Server LAN cần cùng mạng với thiết bị Android thật, đồng thời firewall phải cho phép truy cập cổng `5000`.
- `usesCleartextTraffic=true` đang được bật để hỗ trợ gọi HTTP trong môi trường phát triển.
- Khi thay đổi schema Room, cần cân nhắc migration thay vì chỉ dùng `fallbackToDestructiveMigration()` nếu chuyển sang môi trường production.
