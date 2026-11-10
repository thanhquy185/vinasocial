# UserService — dữ liệu tài khoản và hồ sơ

**Database:** MySQL `vinasocial_user`. Schema được khởi tạo bằng Flyway `V1__initiate.sql`; MFA/OTP được cập nhật ở `V2__add_mfa_and_encrypt_auth_otp.sql`.

## ERD

```mermaid
erDiagram
    users ||--|| profiles : owns
    users ||--o{ usernames : has
    users ||--o{ auth_sessions : creates
    users ||--o{ auth_otps : challenges
    usernames ||--o{ auth_otps : identifies
    profiles ||--o{ profile_links : links
    profiles ||--|| profile_stats : counts
    profiles ||--|| profile_privacy_settings : privacy
    profiles ||--|| profile_notification_settings : notifications

    users {
        int id PK
        varchar public_id UK
        varchar password
        varchar role
        varchar status
        varchar created_at
        varchar last_login_at
        bit two_factor_enabled
    }
    profiles {
        int id PK
        int user_id UK
        varchar username UK
        varchar first_name
        varchar last_name
        varchar alias
        varchar birthdate
        varchar gender
        varchar email UK
        varchar phone UK
        varchar avatar
        text bio
        text note
    }
    usernames {
        int id PK
        int user_id FK
        varchar value UK
        varchar type
        bit is_verify
        bit is_active
    }
    auth_sessions {
        int id PK
        int user_id FK
        varchar username
        mediumtext refresh_token
        varchar created_at
        varchar expired_at
        varchar revoked_at
    }
    auth_otps {
        int id PK
        char public_id UK
        int user_id FK
        int username_id FK
        varchar purpose
        varchar code_encrypted
        int failed_attempts
        varchar created_at
        varchar expired_at
        varchar verified_at
    }
    profile_links {
        int id PK
        int profile_id FK
        varchar title
        varchar url
    }
    profile_stats {
        int profile_id PK
        int media_count
        int friend_count
        int best_friend_count
        int follower_count
        int following_count
        int updated_count
    }
    profile_privacy_settings {
        int profile_id PK
        bit is_private
        varchar show_profile_permission
        varchar show_followings_permission
        varchar show_followers_permission
        varchar show_highlight_stories_permission
        varchar show_posts_permission
        varchar show_reposts_permission
        varchar show_saves_permission
        varchar send_message_permission
    }
    profile_notification_settings {
        int profile_id PK
        bit post_enabled
        bit story_enabled
        bit collaborator_enabled
        bit tag_enabled
        bit message_enabled
        bit follow_enabled
        bit friend_enabled
        bit best_friend_enabled
        bit page_following_enabled
        bit reply_comment_enabled
    }
```

`code_encrypted` là cách gọi mô tả trong sơ đồ; cột thật vẫn tên `auth_otps.code`, được migration V2 nới độ dài để lưu ciphertext.

## Mô tả nghiệp vụ

- `users` là danh tính và thông tin đăng nhập; mỗi user có profile 1:1.
- `usernames` lưu identifier kiểu email, phone hoặc text; mỗi `value` là duy nhất và có trạng thái xác minh/kích hoạt.
- `auth_otps` gắn với user và username, có purpose, hạn dùng, trạng thái xác minh và số lần nhập sai. OTP mới được mã hóa bằng AES-GCM.
- `auth_sessions` theo dõi refresh session, thời hạn và thu hồi.
- `profile_links`, `profile_stats`, `profile_privacy_settings`, `profile_notification_settings` tách dữ liệu phụ khỏi profile chính; các setting/stats dùng `profile_id` làm khóa chính và khóa ngoại.
- Thông tin hồ sơ nhạy cảm được trả khác nhau theo người xem: chi tiết cho chủ tài khoản, trường public đã giới hạn cho người khác.

## Ràng buộc

- `users.public_id`, `profiles.user_id`, `profiles.username`, `usernames.value` là duy nhất.
- Email và phone trong `profiles` có unique constraint nếu có giá trị.
- `auth_otps.purpose` được V2 mở rộng để thêm `ENABLE_MFA` và `DISABLE_MFA`.
- Service khác lưu ID user như dữ liệu tham chiếu; không tạo foreign key từ database khác về `users`.
