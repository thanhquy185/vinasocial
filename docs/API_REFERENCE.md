# API Reference đầy đủ theo Controller

Tài liệu kiểm kê route từ controller của sáu domain service. Gateway local mặc định là `http://localhost:8080`. Route dưới đây là path sau Gateway; xác thực/ủy quyền còn phụ thuộc Security configuration từng service. Response thông thường dùng `RestResponseDTO<T>`; một số tích hợp nội bộ có kiểu đặc thù. Riêng `POST /api/v1/users/info` trả danh sách trực tiếp theo controller.

Query pagination thường dùng `page` (mặc định `0`) và `size` (mặc định do endpoint/service quyết định). Xem DTO/controller để biết chính xác schema request, response và validation.

## UserService — xác thực, hồ sơ, tài khoản

### AuthController — `/api/v1/auth`

| Method | Path | Nghiệp vụ |
|---|---|---|
| POST | `/register` | Đăng ký tài khoản. |
| POST | `/get-info` | Tra cứu thông tin phục vụ bước xác thực. |
| POST | `/login` | Đăng nhập; có thể khởi chạy OTP challenge khi MFA bật. |
| POST | `/login/verify-otp` | Xác minh OTP challenge đăng nhập. |
| GET | `/mfa` | Lấy trạng thái MFA hiện tại. |
| POST | `/mfa/enable` | Bắt đầu bật MFA. |
| POST | `/mfa/enable/verify` | Xác minh để hoàn tất bật MFA. |
| POST | `/mfa/disable` | Bắt đầu tắt MFA. |
| POST | `/mfa/disable/verify` | Xác minh để hoàn tất tắt MFA. |
| POST | `/refresh-token` | Làm mới access token. |
| POST | `/logout` | Đăng xuất / thu hồi phiên theo thiết kế token. |

### AuthOtpController — `/api/v1/auth/otp`

| Method | Path | Nghiệp vụ |
|---|---|---|
| GET | `/{publicId}` | Đọc trạng thái/thông tin challenge qua public ID. |
| POST | `/forgot-password` | Bắt đầu khôi phục mật khẩu bằng OTP. |
| POST | `/reset-password` | Đặt mật khẩu mới theo challenge/OTP. |
| POST | `/verify` | Xác minh OTP theo luồng chung. |
| POST | `/resend/{publicId}` | Gửi lại OTP cho challenge. |

### UserController — `/api/v1/users`

| Method | Path | Nghiệp vụ |
|---|---|---|
| GET | `/user-test-connect` | Kiểm tra kết nối service. |
| POST | `/info` | Tra cứu nhiều user theo IDs; response là `List`, không phải envelope theo contract controller hiện tại. |
| GET | `/profile/{username}` | Đọc hồ sơ theo viewer; chủ tài khoản có thể nhận detail, người khác public profile. |
| PUT | `/profile` | Cập nhật thông tin trang cá nhân. |
| PUT | `/profile/personal-information` | Cập nhật thông tin cá nhân riêng. |
| PUT | `/profile/privacy` | Cập nhật setting riêng tư theo request riêng. |
| PUT | `/profile/notifications` | Cập nhật setting thông báo theo request riêng. |
| GET | `/{id}` | Đọc tài khoản theo ID. |
| GET | `` (root) | Danh sách/tìm kiếm tài khoản. |
| POST | `` (root) | Tạo tài khoản qua luồng quản trị. |
| PATCH | `/{id}` | Cập nhật tài khoản theo request quản trị. |
| PATCH | `/{id}/status` | Đổi trạng thái tài khoản. |
| DELETE | `/{id}` | Xóa tài khoản. |

## ContentService — post, story, moderation

### PostController — `/api/v1/posts`

| Method | Path | Nghiệp vụ |
|---|---|---|
| GET | `/profile/{username}` | Danh sách post của hồ sơ. |
| GET | `/{id}` | Chi tiết post. |
| GET | `` (root) | Truy vấn danh sách/bảng tin post theo filter và pagination request. |
| POST | `` (root) | Tạo post và liên kết media. |
| PUT | `/{id}` | Cập nhật post. |

### StoryController — `/api/v1/stories`

| Method | Path | Nghiệp vụ |
|---|---|---|
| GET | `/feed` | Story feed đủ điều kiện xem. |
| GET | `/me` | Story của tài khoản hiện tại. |
| GET | `/users/{username}` | Story đang hiển thị của user. |
| POST | `` (root) | Tạo story. |
| PUT | `/{id}/settings` | Cập nhật thiết lập story. |
| DELETE | `/{id}` | Xóa story. |

### AdminPostController — `/api/v1/admin/posts`

| Method | Path | Nghiệp vụ |
|---|---|---|
| GET | `` (root) | Danh sách post phục vụ quản trị. |
| GET | `/{id}` | Chi tiết post trong khu vực quản trị. |
| PATCH | `/{id}/status` | Cập nhật trạng thái post. |
| PATCH | `/{id}/moderation-status` | Cập nhật trạng thái kiểm duyệt. |

### AdminStoryController — `/api/v1/admin/stories`

| Method | Path | Nghiệp vụ |
|---|---|---|
| GET | `` (root) | Danh sách story phục vụ quản trị. |
| GET | `/{id}` | Chi tiết story trong khu vực quản trị. |
| PATCH | `/{id}/status` | Cập nhật trạng thái story. |
| PATCH | `/{id}/moderation-status` | Cập nhật trạng thái kiểm duyệt. |

## RelationshipService — follow, block, conversations, messages

### RelationshipController — `/api/v1/relationships`

| Method | Path | Nghiệp vụ |
|---|---|---|
| POST | `/users/{username}/follow` | Follow hoặc tạo follow request tùy privacy. |
| DELETE | `/users/{username}/follow` | Unfollow hoặc hủy follow request. |
| GET | `/me/followers` | Danh sách follower. |
| GET | `/me/following` | Danh sách đang follow. |
| GET | `/me/follow-requests` | Follow request đến. |
| POST | `/follow-requests/{requestId}/accept` | Chấp nhận follow request. |
| POST | `/follow-requests/{requestId}/reject` | Từ chối follow request. |
| DELETE | `/follow-requests/{requestId}` | Hủy follow request. |
| GET | `/users/{username}/relationship` | Trạng thái quan hệ với user. |
| POST | `/me/story-audience/{visibility}` | Với danh sách owner IDs trong body, trả về IDs mà viewer hiện tại đủ quyền xem story theo visibility đó. Đây là truy vấn audience, không phải ghi setting. |
| GET | `/me/close-friends` | Danh sách Close Friends. |
| PUT | `/me/close-friends/{username}` | Thêm Close Friend. |
| DELETE | `/me/close-friends/{username}` | Xóa Close Friend. |
| POST | `/users/{username}/block` | Chặn user. |
| DELETE | `/users/{username}/block` | Bỏ chặn user. |
| GET | `/me/blocks` | Danh sách user bị chặn. |

### ConversationController — `/api/v1/conversations`

| Method | Path | Nghiệp vụ |
|---|---|---|
| GET | `/me` | Danh sách hội thoại của mình. |
| PATCH | `/me/read` | Đánh dấu các hội thoại của mình đã đọc theo semantics request/service. |
| PATCH | `/{conversationId}/read` | Đánh dấu một hội thoại đã đọc. |
| GET | `/me/requests` | Message request đến. |
| GET | `/me/sent-requests` | Message request đã gửi. |
| POST | `/requests/{requestId}/accept` | Chấp nhận message request. |
| POST | `/requests/{requestId}/reject` | Từ chối message request. |
| DELETE | `/requests/{requestId}` | Hủy message request. |
| POST | `/groups` | Tạo group conversation. |
| GET | `/{conversationId}/participants` | Danh sách thành viên hội thoại. |
| POST | `/{conversationId}/participants/{username}` | Thêm thành viên vào group. |
| PATCH | `/{conversationId}/participants/{username}/role` | Cập nhật role thành viên. |
| DELETE | `/{conversationId}/participants/me` | Rời hội thoại. |

### MessageController — `/api/v1`

| Method | Path | Nghiệp vụ |
|---|---|---|
| POST | `/users/{username}/messages` | Gửi tin nhắn trực tiếp; có thể thành message request. |
| POST | `/conversations/{conversationId}/messages` | Gửi tin trong hội thoại. |
| GET | `/conversations/{conversationId}/messages` | Đọc lịch sử tin nhắn. |
| PATCH | `/conversations/{conversationId}/messages/{messageId}` | Sửa tin nhắn. |
| DELETE | `/conversations/{conversationId}/messages/{messageId}` | Xóa tin nhắn. |

## InteractionService — like, comment, view, save, share, report

| Controller | Method | Path | Nghiệp vụ |
|---|---|---|---|
| InteractionQuery | GET | `/api/v1/interactions/targets/{targetType}/{targetId}/summary` | Tổng hợp số liệu tương tác cho target. |
| Like | POST | `/api/v1/interactions/likes/{targetType}/{targetId}` | Thích target. |
| Like | DELETE | `/api/v1/interactions/likes/{targetType}/{targetId}` | Bỏ thích. |
| Like | GET | `/api/v1/interactions/likes/{targetType}/{targetId}/me` | Trạng thái like của viewer. |
| Like | GET | `/api/v1/interactions/likes/{targetType}/{targetId}` | Danh sách lượt thích. |
| Comment | POST | `/api/v1/interactions/comments` | Tạo comment hoặc reply theo request. |
| Comment | GET | `/api/v1/interactions/comments` | Danh sách comment theo query parameters. |
| Comment | GET | `/api/v1/interactions/comments/{commentId}` | Chi tiết comment. |
| Comment | GET | `/api/v1/interactions/comments/{commentId}/replies` | Danh sách reply. |
| Comment | PUT | `/api/v1/interactions/comments/{commentId}` | Sửa comment. |
| Comment | DELETE | `/api/v1/interactions/comments/{commentId}` | Xóa comment. |
| Comment | PATCH | `/api/v1/interactions/comments/{commentId}/status` | Đổi trạng thái comment. |
| View | POST | `/api/v1/interactions/views/{targetType}/{targetId}` | Ghi nhận lượt xem. |
| View | POST | `/api/v1/interactions/views/{targetType}/me` | Lấy IDs target đã xem; controller dùng POST/body. |
| View | GET | `/api/v1/interactions/views/{targetType}/{targetId}` | Danh sách viewer. |
| Reup | POST | `/api/v1/interactions/reups/{postId}` | Đăng lại post. |
| Reup | DELETE | `/api/v1/interactions/reups/{postId}` | Gỡ đăng lại. |
| Reup | GET | `/api/v1/interactions/reups/me` | Danh sách post đã đăng lại. |
| Save | POST | `/api/v1/interactions/saves/{postId}` | Lưu post. |
| Save | DELETE | `/api/v1/interactions/saves/{postId}` | Bỏ lưu post. |
| Save | GET | `/api/v1/interactions/saves/{postId}/me` | Trạng thái đã lưu. |
| Save | GET | `/api/v1/interactions/saves/me` | Danh sách post đã lưu. |
| Share | POST | `/api/v1/interactions/shares` | Tạo lượt chia sẻ. |
| Share | GET | `/api/v1/interactions/shares` | Truy vấn lượt chia sẻ. |
| Report | POST | `/api/v1/interactions/reports` | Tạo báo cáo nội dung/tài khoản. |
| Report | GET | `/api/v1/interactions/reports/me` | Báo cáo do mình gửi. |
| Report | GET | `/api/v1/interactions/reports/admin` | Danh sách báo cáo cho quản trị. |
| Report | PATCH | `/api/v1/interactions/reports/{reportId}/status` | Cập nhật trạng thái xử lý báo cáo. |

`targetType` hợp lệ và filter cụ thể được định nghĩa bởi enum/DTO trong InteractionService.

## MediaService — `/api/v1/medias`

| Method | Path | Nghiệp vụ |
|---|---|---|
| POST | `/validate` | Validate danh sách media theo request JSON; controller trả boolean trực tiếp. |
| POST | `/info` | Lấy metadata nhiều media IDs; controller trả danh sách trực tiếp. |
| POST | `` (root) | Tạo/upload một media qua multipart. |
| POST | `/is-medias` | Tạo/upload nhiều media qua multipart. |

Tên multipart fields và giới hạn file là một phần của DTO/controller, không được suy ra từ route.

## NotificationService — `/api/v1/notifications`

| Method | Path | Nghiệp vụ |
|---|---|---|
| GET | `/me` | Danh sách notification của mình; hỗ trợ paging/filter theo query. |
| GET | `/me/unread-count` | Đếm notification chưa đọc. |
| PATCH | `/me/{notificationId}/read` | Đánh dấu một notification đã đọc. |
| PATCH | `/me/read-all` | Đánh dấu đã đọc toàn bộ. |
| DELETE | `/me/{notificationId}` | Xóa/ẩn notification khỏi inbox của mình. |
| POST | `/notification-test-connect` | Kiểm tra kết nối service. |

## Tình trạng triển khai

API route có trong controller không đồng nghĩa đã được nối vào mọi màn hình website hoặc đã có đầy đủ UX. Đối chiếu [Service catalog](business/SERVICE_CATALOG.md) và [Use cases](business/USE_CASES.md) để phân biệt service capability, website integration và giới hạn đã xác nhận. OpenAPI Gateway aggregate specs dưới `/v3/api-docs/{service-name}`.
