# ContentService — bài viết và tin

**Database:** PostgreSQL `vinasocial_content`. Flyway migrations: `V1__initiate.sql`, `V2__create_stories.sql`.

## ERD

```mermaid
erDiagram
    posts ||--|| post_stats : stats
    posts ||--|| post_settings : settings
    posts ||--o{ post_medias : includes
    stories ||--|| story_stats : stats
    stories ||--|| story_settings : settings

    posts {
        int id PK
        varchar public_id UK
        int user_id
        varchar created_at
        varchar type
        text caption
        varchar moderation_status
        varchar status
    }
    post_stats {
        int post_id PK
        int view_count
        int like_count
        int comment_count
        int reup_count
        int share_count
        int save_count
    }
    post_settings {
        int post_id PK
        varchar privacy
        boolean is_pinned
        boolean allow_comment
        boolean allow_reup
        boolean allow_share
        boolean allow_save
        boolean show_view_count
        boolean show_like_count
        boolean show_comment_count
        boolean show_reup_count
        boolean show_share_count
        boolean show_save_count
    }
    post_medias {
        int id PK
        varchar media_id
        int post_id FK
    }
    stories {
        int id PK
        varchar public_id UK
        int user_id
        varchar media_id
        varchar created_at
        varchar expired_at
        varchar status
        varchar moderation_status
    }
    story_stats {
        int story_id PK
        int view_count
        int reply_count
        int share_count
    }
    story_settings {
        int story_id PK
        varchar visibility
        boolean allow_like
        boolean allow_reply
        boolean allow_share
    }
```

## Mô tả nghiệp vụ

- `posts` chứa tác giả, loại bài, caption, status và moderation status. Settings và stats là bảng 1:1 theo khóa `post_id`.
- `post_medias` lưu thứ tự/liên kết media qua media ID. FK chỉ tới `posts`; `media_id` thuộc MediaService nên không có FK cross-service.
- `stories` trỏ user/media bằng ID logic và có `expired_at`; bảng riêng lưu audience/khả năng like, reply, share và thống kê.
- Feed/profile query cần lọc status, moderation, hạn story và privacy/audience. Một số quyền được kiểm tra qua UserService và RelationshipService.
- Admin routes quản lý trạng thái và moderation cho post/story.

## Ràng buộc và ghi chú

- `public_id` duy nhất cho post/story.
- `post_stats`, `post_settings`, `story_stats`, `story_settings` dùng PK đồng thời làm FK tới entity cha.
- Story stats/settings có `ON DELETE CASCADE`; các FK của post trong migration V1 chưa khai báo cascade.
- `ReelEntity` hiện rỗng và không có bảng/migration Reel trong schema ContentService hiện tại.
