# Sequence nghiệp vụ chính

Các sơ đồ thể hiện thành phần và thứ tự xử lý ở mức service. Tên endpoint lấy từ controller hiện có; chi tiết payload nằm trong DTO tương ứng.

## Đăng nhập khi bật 2FA

```mermaid
sequenceDiagram
    actor U as Thành viên
    participant W as Website
    participant G as Gateway
    participant US as UserService
    participant DB as MySQL User
    participant K as Kafka
    participant NS as NotificationService
    U->>W: Nhập identifier và mật khẩu
    W->>G: POST /api/v1/auth/login
    G->>US: Chuyển request
    US->>DB: Kiểm tra user và trạng thái MFA
    alt MFA tắt
        US-->>G: Token đăng nhập theo luồng thường
        G-->>W: Response đăng nhập
    else MFA bật
        US->>DB: Tạo challenge OTP đã mã hóa, có hạn dùng
        US->>K: Phát event auth_otp_security
        K->>NS: Chuyển event
        NS-->>U: Gửi OTP qua email/SMS đã xác minh
        US-->>G: Trả publicId, trạng thái cần OTP và nơi nhận đã che
        G-->>W: Response cần OTP
        U->>W: Nhập OTP
        W->>G: POST /api/v1/auth/login/verify-otp
        G->>US: Chuyển publicId và code
        US->>DB: Kiểm tra purpose, hạn dùng, số lần sai và verifiedAt
        US-->>G: Access token và refresh cookie nếu hợp lệ
        G-->>W: Response xác thực thành công
    end
```

## Xem trang cá nhân và bài viết

```mermaid
sequenceDiagram
    actor V as Người xem
    participant W as Website
    participant G as Gateway
    participant US as UserService
    participant CS as ContentService
    participant R as RelationshipService
    V->>W: Mở /{username}
    W->>G: GET /api/v1/users/profile/{username}
    G->>US: Chuyển request cùng thông tin xác thực (nếu có)
    US->>US: So sánh người xem với chủ hồ sơ và áp dụng privacy
    US-->>G: ProfileDetail nếu chính chủ; dữ liệu public nếu người khác
    G-->>W: Response profile
    W->>G: GET /api/v1/posts/profile/{username}
    G->>CS: Chuyển request
    CS->>US: Lấy user/profile/quyền xem qua UserClient
    opt Quyết định quyền phụ thuộc quan hệ
        CS->>R: Lấy trạng thái follow/audience cần thiết
        R-->>CS: Trạng thái quan hệ
    end
    CS->>CS: Lọc status, moderation và privacy bài viết
    CS-->>G: Danh sách bài viết được phép xem
    G-->>W: Response bài viết
    W-->>V: Hiển thị hồ sơ và nội dung
```

## Cập nhật hồ sơ và ảnh đại diện

```mermaid
sequenceDiagram
    actor U as Chủ tài khoản
    participant W as Website
    participant G as Gateway
    participant MS as MediaService
    participant M as MongoDB / Media provider
    participant US as UserService
    participant DB as MySQL User
    U->>W: Chọn ảnh và cập nhật form
    W->>G: POST /api/v1/medias (multipart)
    G->>MS: Chuyển file và access token
    MS->>M: Lưu media metadata
    MS->>M: Tải object lên provider
    MS-->>G: Trả media ID
    G-->>W: Response upload
    W->>G: PUT /api/v1/users/profile (media ID + profile fields)
    G->>US: Chuyển request
    US->>MS: Validate media ID, owner và purpose
    MS-->>US: Kết quả validate
    US->>DB: Cập nhật profile/link
    US-->>G: ProfileDetailResponse
    G-->>W: Response cập nhật hồ sơ
    W-->>U: Báo lưu thành công
```

Thông tin cá nhân, privacy setting và notification setting dùng các endpoint/request riêng trong UserService; không gộp chúng vào request cập nhật profile.

## Follow tài khoản riêng tư

```mermaid
sequenceDiagram
    actor A as Người theo dõi
    participant W as Website
    participant G as Gateway
    participant RS as RelationshipService
    participant US as UserService
    participant DB as MySQL Relationship
    actor B as Chủ tài khoản
    A->>W: Nhấn Theo dõi username B
    W->>G: POST /api/v1/relationships/users/{username}/follow
    G->>RS: Chuyển request
    RS->>US: Đọc profile và trạng thái private
    US-->>RS: userId và privacy setting
    alt Tài khoản công khai
        RS->>DB: Tạo follows(followerId, followingId)
        RS-->>G: Quan hệ active
        G-->>W: Response follow
    else Tài khoản riêng tư
        RS->>DB: Tạo follow_requests(PENDING)
        RS-->>G: Trạng thái chờ duyệt
        G-->>W: Response chờ duyệt
        B->>W: Chấp nhận yêu cầu
        W->>G: POST /api/v1/relationships/follow-requests/{id}/accept
        G->>RS: Chuyển request
        RS->>DB: Đánh dấu accepted và tạo follow edge
        RS-->>G: Quan hệ active
        G-->>W: Response follow accepted
    end
```

## Gửi tin nhắn lần đầu

```mermaid
sequenceDiagram
    actor A as Người gửi
    participant W as Website
    participant G as Gateway
    participant RS as RelationshipService
    participant DB as MySQL Relationship
    actor B as Người nhận
    A->>W: Gửi message đầu cho B
    W->>G: POST /api/v1/users/{username}/messages
    G->>RS: Chuyển request
    RS->>RS: Kiểm tra block và quan hệ follow
    alt Hai bên mutual-follow
        RS->>DB: Tạo/tìm direct conversation, thêm message
        RS-->>G: Message trong inbox hoạt động
        G-->>W: Response gửi tin nhắn
    else Không mutual-follow
        RS->>DB: Tạo conversation + conversation_request + message đầu
        RS-->>G: Trạng thái message request
        G-->>W: Response message request
        B->>W: Chấp nhận request
        W->>G: POST /api/v1/conversations/requests/{id}/accept
        G->>RS: Chuyển request
        RS->>DB: Thêm B làm participant, accept request
        RS-->>G: Conversation hoạt động
        G-->>W: Response request accepted
    end
```

## Tạo bài viết có media

```mermaid
sequenceDiagram
    actor U as Thành viên
    participant W as Website
    participant G as Gateway
    participant MS as MediaService
    participant CS as ContentService
    participant DB as PostgreSQL Content
    U->>W: Chọn media và nhập nội dung
    W->>G: POST /api/v1/medias (multipart)
    G->>MS: Chuyển file
    MS-->>G: Danh sách media ID
    G-->>W: Response upload
    W->>G: POST /api/v1/posts (caption, setting, media IDs)
    G->>CS: Chuyển request
    CS->>MS: Validate quyền sở hữu và purpose media
    MS-->>CS: Trạng thái media
    CS->>DB: Tạo post, settings, stats, post_medias
    CS-->>G: Post response envelope
    G-->>W: Response tạo post
    W-->>U: Hiển thị bài viết mới
```

## Phát notification từ event

```mermaid
sequenceDiagram
    participant Producer as Service phát event
    participant K as Kafka
    participant NS as NotificationService consumer
    participant DB as MongoDB Notification
    participant W as Website
    participant G as Gateway
    actor U as Thành viên
    Producer->>K: Publish notification event
    K->>NS: Consumer đọc event
    NS->>DB: Kiểm tra eventKey và notification preference
    alt Được phép và chưa xử lý
        NS->>DB: Lưu notification
    else Không được phép hoặc event trùng
        NS->>NS: Bỏ qua hoặc dừng tạo bản ghi trùng
    end
    U->>W: Mở hộp thư thông báo
    W->>G: GET /api/v1/notifications/me
    G->>NS: Chuyển request
    NS->>DB: Truy vấn inbox của user
    DB-->>NS: Danh sách notification
    NS-->>G: Response envelope
    G-->>W: Response notification
    W-->>U: Hiển thị danh sách
```

## Khôi phục mật khẩu bằng OTP

```mermaid
sequenceDiagram
    actor U as Thành viên
    participant W as Website
    participant G as Gateway
    participant US as UserService
    participant DB as MySQL User
    participant K as Kafka / OTP delivery event
    participant N as NotificationService / provider
    U->>W: Nhập email hoặc số điện thoại
    W->>G: POST /api/v1/auth/otp/forgot-password
    G->>US: Chuyển request
    US->>DB: Kiểm tra account và tạo OTP challenge
    US->>K: Phát yêu cầu gửi OTP
    K->>N: Consumer xử lý delivery
    N-->>U: Gửi OTP qua kênh đã xác minh (nếu cấu hình)
    US-->>G: Challenge publicId / response
    G-->>W: Response khởi tạo
    U->>W: Nhập OTP và mật khẩu mới
    W->>G: POST /api/v1/auth/otp/reset-password
    G->>US: Chuyển challenge, OTP, mật khẩu mới
    US->>DB: Kiểm tra purpose, hạn dùng, attempts và đặt password mới
    US-->>G: Kết quả reset
    G-->>W: Response
    W-->>U: Hiển thị kết quả
```

## Tương tác với bài viết và gửi báo cáo

```mermaid
sequenceDiagram
    actor U as Thành viên
    participant W as Website
    participant G as Gateway
    participant C as ContentService
    participant I as InteractionService
    participant DB as Database Interaction
    U->>W: Mở post hoặc story
    W->>G: GET nội dung target
    G->>C: Đọc target và kiểm tra quyền xem
    C-->>G: Nội dung đủ điều kiện
    G-->>W: Target response
    W->>G: POST /interactions/views/{type}/{id}
    G->>I: Ghi nhận lượt xem
    I->>DB: Lưu view theo target/user
    U->>W: Like / comment / save / report
    W->>G: POST endpoint interaction tương ứng
    G->>I: Chuyển request và xác thực viewer
    I->>DB: Ghi interaction và cập nhật dữ liệu tổng hợp nếu cần
    I-->>G: Kết quả interaction
    G-->>W: Response
    W-->>U: Cập nhật trạng thái giao diện
```

## Quản trị xử lý nội dung và report

```mermaid
sequenceDiagram
    actor U as Người báo cáo
    actor A as Quản trị viên
    participant W as Website
    participant G as Gateway
    participant I as InteractionService
    participant C as ContentService
    participant DBI as Database Interaction
    participant DBC as PostgreSQL Content
    U->>W: Gửi report target
    W->>G: POST /api/v1/interactions/reports
    G->>I: Tạo report
    I->>DBI: Lưu report và trạng thái ban đầu
    I-->>W: Report response
    A->>W: Mở hàng đợi report
    W->>G: GET /api/v1/interactions/reports/admin
    G->>I: Truy vấn report queue
    I->>DBI: Đọc danh sách theo filter/page
    I-->>W: Reports
    A->>W: Cập nhật kết quả xử lý report
    W->>G: PATCH /api/v1/interactions/reports/{id}/status
    G->>I: Cập nhật trạng thái report
    I->>DBI: Lưu trạng thái xử lý
    opt Hành động kiểm duyệt nội dung được chọn
        W->>G: PATCH /api/v1/admin/posts/{id}/moderation-status hoặc story tương ứng
        G->>C: Cập nhật moderation status
        C->>DBC: Lưu moderation state
        C-->>W: Kết quả moderation
    end
```

## Tạo group conversation, thêm thành viên và rời nhóm

```mermaid
sequenceDiagram
    actor A as Thành viên tạo nhóm
    actor B as Thành viên được mời
    participant W as Website
    participant G as Gateway
    participant R as RelationshipService
    participant DB as MySQL Relationship
    A->>W: Chọn tên nhóm và thành viên
    W->>G: POST /api/v1/conversations/groups
    G->>R: Tạo conversation/group và participants
    R->>DB: Lưu conversation, membership, roles
    R-->>G: Conversation summary
    G-->>W: Group created
    A->>W: Gửi tin nhắn nhóm
    W->>G: POST /api/v1/conversations/{id}/messages
    G->>R: Tạo message sau khi kiểm tra membership
    R->>DB: Lưu opaque message payload và trạng thái đọc
    R-->>W: Message response
    B->>W: Mở group và đọc tin
    W->>G: GET messages + PATCH /{id}/read
    G->>R: Đọc history, cập nhật read state
    R->>DB: Truy vấn/cập nhật
    opt Quản trị viên nhóm thay đổi thành viên
        W->>G: POST /{id}/participants/{username} hoặc PATCH role
        G->>R: Kiểm tra quyền và cập nhật membership
        R->>DB: Ghi membership/role
    end
    opt Thành viên rời nhóm
        W->>G: DELETE /{id}/participants/me
        G->>R: Kiểm tra membership và rời conversation
        R->>DB: Cập nhật participant state
    end
```

## Cập nhật một loại setting hồ sơ

```mermaid
sequenceDiagram
    actor U as Chủ tài khoản
    participant W as Website
    participant G as Gateway
    participant US as UserService
    participant DB as MySQL User
    U->>W: Sửa trường profile/privacy/notification/personal info
    W->>G: PUT endpoint chuyên biệt của loại dữ liệu
    G->>US: Chuyển request DTO tương ứng
    US->>US: Xác thực quyền sở hữu và validate request
    US->>DB: Cập nhật đúng nhóm trường được yêu cầu
    US-->>G: Response hồ sơ/setting
    G-->>W: Response envelope
    W-->>U: Cập nhật UI và cache liên quan
```

## Ghi chú về sơ đồ

- Đây là luồng khái niệm dựa trên controller, service, client và thiết kế hiện tại; tên bảng lưu đã được giản lược. Dùng database docs cho tên collection/table thực tế.
- Sơ đồ không thay thế authorization matrix. Điều kiện truy cập đầy đủ phải kiểm tra Security configuration và từng service method.
- Các mũi tên event/provider biểu diễn kiến trúc dự kiến/current integration; trạng thái gửi OTP, event phát sinh và preference filtering phụ thuộc config/producer thực tế.
- Website mã hóa payload trước khi gửi; RelationshipService chỉ lưu envelope ciphertext và server không giải mã. Key registry theo thiết bị đã có, nhưng prototype hiện chưa thiết lập khóa theo protocol có forward secrecy hoặc xác minh fingerprint; xem [RelationshipService design](../../server/RelationshipService/DESIGN.md).
