# Cơ sở dữ liệu theo service

Các sơ đồ dưới đây được dựng từ Flyway migration, Mongock migration và entity/collection hiện có. Chúng mô tả cấu trúc ở source, không thay thế schema đang chạy trong từng môi trường.

| Service | Database | Tài liệu |
|---|---|---|
| UserService | MySQL | [USER_SERVICE.md](USER_SERVICE.md) |
| ContentService | PostgreSQL | [CONTENT_SERVICE.md](CONTENT_SERVICE.md) |
| RelationshipService | MySQL | [RELATIONSHIP_SERVICE.md](RELATIONSHIP_SERVICE.md) |
| InteractionService | MongoDB | [INTERACTION_SERVICE.md](INTERACTION_SERVICE.md) |
| MediaService | MongoDB | [MEDIA_SERVICE.md](MEDIA_SERVICE.md) |
| NotificationService | MongoDB | [NOTIFICATION_SERVICE.md](NOTIFICATION_SERVICE.md) |

ConfigService, DiscoveryService, GatewayService và MonitorAdmin không có schema nghiệp vụ riêng được khai báo trong các migration/entity của ứng dụng hiện tại. Eureka giữ registry service; cấu hình được nạp từ ConfigService. Hạ tầng có thể có storage riêng bên ngoài source này.

## Cách đọc sơ đồ

- ERD quan hệ biểu diễn foreign key thật khi được tạo bởi migration.
- ID như `user_id`, `media_id`, `post_id` khi nằm trong service khác thường là tham chiếu logic qua API, không phải foreign key xuyên service.
- Với MongoDB, sơ đồ biểu diễn collection và liên kết logic; MongoDB không tự áp đặt foreign key relational.
- Trường `lastReadMessageId` trong conversation participant trỏ logic tới message; migration hiện không tạo foreign key cho trường này.
