# Hướng dẫn phát triển và chạy local

## Công cụ cần có

- Node.js và npm tương thích với project website.
- Java 21 cho backend.
- MySQL, PostgreSQL, MongoDB và Kafka theo các địa chỉ được cấu hình trong `server/ConfigService/src/main/resources/shared/`.
- Nhà cung cấp OIDC/JWT phù hợp với issuer cấu hình trong Gateway. Config hiện trỏ issuer tới `http://localhost:8080/realms/vinasocial-app`; kiểm tra cấu hình identity provider đang dùng trước khi chạy các endpoint bảo vệ.
- Các tích hợp ngoài nếu dùng tính năng tương ứng: Cloudinary, SMTP và SMS/Twilio.

## Chạy website

```sh
cd client/website
npm install
```

Tạo `client/website/.env.local`:

```dotenv
NEXT_PUBLIC_API_URL=http://localhost:8080
```

Khởi động dev server:

```sh
npm run dev
```

Các lệnh hiện có trong `package.json`:

| Lệnh | Mục đích |
|---|---|
| `npm run dev` | Chạy Next.js ở chế độ phát triển |
| `npm run build` | Build production |
| `npm run start` | Chạy bản production đã build |
| `npm run lint` | Chạy ESLint |
| `npm run scss:start` | Biên dịch SCSS và theo dõi thay đổi |

## Chuẩn bị thư viện backend dùng chung

Mỗi backend service là Gradle project riêng. Các service tham chiếu `vn.tuhoc:core` và `vn.tuhoc:security` qua `mavenLocal()`, nên xuất bản thư viện dùng chung trước khi build service nếu máy chưa có artifact tương ứng:

```sh
cd server/common
./gradlew publishToMavenLocal
```

`common/security` phụ thuộc `common/core` trong cùng Gradle build. Nếu gặp lỗi không tìm thấy artifact nội bộ, kiểm tra kết quả publish và Maven Local trước.

## Thứ tự khởi động backend

1. Khởi động database, Kafka và các dịch vụ ngoài cần thiết.
2. Khởi động `ConfigService` ở cổng 8888.
3. Khởi động `DiscoveryService` ở cổng 8761.
4. Khởi động backend cần thiết; ví dụ UserService, RelationshipService, ContentService, InteractionService, MediaService và NotificationService.
5. Khởi động `GatewayService` ở cổng 8080.
6. Khởi động `MonitorAdmin` nếu cần giám sát.

Ví dụ chạy một service:

```sh
cd server/UserService
./gradlew bootRun
```

Build/test một service:

```sh
cd server/UserService
./gradlew build
```

Thay `UserService` bằng thư mục service cần chạy. Backend không có wrapper tổng ở thư mục `server`; mỗi module chứa wrapper riêng.

## Cấu hình lưu trữ local mặc định

| Service | Mặc định trong config |
|---|---|
| UserService | MySQL `localhost:3306/vinasocial_user` |
| RelationshipService | MySQL `localhost:3306/vinasocial_relationship` |
| ContentService | PostgreSQL `localhost:5432/vinasocial_content` |
| InteractionService | MongoDB `localhost:27017/vinasocial_interaction` |
| MediaService | MongoDB `localhost:27017/vinasocial` |
| NotificationService | MongoDB `localhost:27017/vinasocial_notification` |
| Kafka | `localhost:9092` |
| Zipkin | `localhost:9411` |

Một số biến môi trường được cấu hình hỗ trợ: `SPRING_DATASOURCE_HOST`, `SPRING_DATASOURCE_PORT`, `SPRING_DATASOURCE_DATABASE`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `SPRING_DATA_MONGODB_URI`, `MONGODB_HOST`, `MONGODB_DATABASE` và `AUTH_OTP_ENCRYPTION_KEY_BASE64`. Tên biến có thể chỉ áp dụng cho một số service; đối chiếu YAML service trước khi chạy.

## OTP encryption key

UserService cần khóa AES-256 dạng Base64, đại diện đúng 32 byte. Hướng dẫn đầy đủ về giữ khóa, file local và các giới hạn nằm trong [README của UserService](../server/UserService/README.md) và [tài liệu 2FA](2FA-IMPLEMENTATION.md). Không tạo khóa mới cho database đã có OTP mã hóa và không commit khóa.

## Bí mật cấu hình

Không chép credential vào tài liệu hoặc commit credential thật. Các cấu hình JWT, Cloudinary, SMTP và Twilio trong repository lấy giá trị từ biến môi trường:

| Thành phần | Biến môi trường |
|---|---|
| JWT dùng chung | `JWT_BASE64_SECRET` |
| MediaService / Cloudinary | `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` |
| NotificationService / SMTP và MonitorAdmin | `MAIL_USERNAME`, `MAIL_PASSWORD` |
| NotificationService / Twilio | `TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN`, `TWILIO_PHONE_FROM` |

Đặt các biến tương ứng trước khi khởi động service cần dùng. Không lưu giá trị thật trong file được commit. Nếu những giá trị từng được ghi trực tiếp trong source còn hiệu lực, hãy thay hoặc thu hồi chúng ở nhà cung cấp dịch vụ; sửa lịch sử Git không làm vô hiệu hóa credential cũ.

## Migration và API docs

Các service dùng Flyway hoặc Mongock tùy module. Migration tự chạy theo cấu hình service khi khởi động; kiểm tra thư mục `src/main/resources/db/migration` của service trước khi cập nhật database.

Gateway tổng hợp OpenAPI cho User, Relationship, Content, Interaction, Media và Notification. Xem cấu hình `springdoc` trong `server/ConfigService/src/main/resources/shared/gateway-service.yml`; đường dẫn Swagger UI phụ thuộc cấu hình runtime của Gateway.

## Chẩn đoán nhanh

- Website không gọi được API: kiểm tra `NEXT_PUBLIC_API_URL`, Gateway và trạng thái service trong Eureka.
- Service không khởi động: kiểm tra ConfigService trước, sau đó kiểm tra database/broker mà service cần.
- UserService lỗi ở lớp mã hóa OTP: kiểm tra `AUTH_OTP_ENCRYPTION_KEY_BASE64` hoặc file local theo README UserService.
- Lỗi xác thực JWT: đối chiếu issuer của token với `issuer-uri` mà Gateway và các resource server đang dùng.
- Lỗi Feign: kiểm tra cả kiểu dữ liệu trả về của endpoint nguồn lẫn wrapper/DTO generic mà Feign client mong đợi.

## Tình trạng kiểm tra đã ghi nhận

Lịch sử làm việc ngày 15/10/2026 ghi nhận các backend UserService, ContentService và GatewayService compile thành công trong luồng hồ sơ; lint các file frontend liên quan cũng qua. TypeScript toàn project ở thời điểm đó còn lỗi tại admin, create-post, feed và OTP.

Lịch sử làm việc ngày 20/10/2026 ghi nhận chưa build xác nhận phần sửa `noRollbackFor` do Gradle không ghi được cache, và chưa khởi động lại UserService sau khi cấu hình khóa local. Hãy chạy build và khởi động thực tế trước khi coi các thay đổi 2FA đã được xác minh end-to-end.
