-- Demo data for the 20 profile-backed users seeded by UserService V1 (user IDs 2-21).
-- User ID 1 is the admin account and has no profile in that seed migration.
-- RelationshipService intentionally has no cross-service foreign keys to UserService.

SET @relationship_seed_now = NOW();

-- Directed follow graph. Mutual pairs represent derived friendships.
INSERT IGNORE INTO follows (follower_id, following_id, created_at)
SELECT seed.follower_id,
       seed.following_id,
       DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 20 DAY), '%Y-%m-%d %H:%i:%s')
FROM (
    SELECT 2 AS follower_id, 3 AS following_id
    UNION ALL SELECT 3, 2
    UNION ALL SELECT 2, 4
    UNION ALL SELECT 4, 2
    UNION ALL SELECT 3, 4
    UNION ALL SELECT 4, 3
    UNION ALL SELECT 5, 6
    UNION ALL SELECT 6, 5
    UNION ALL SELECT 7, 8
    UNION ALL SELECT 8, 7
    UNION ALL SELECT 6, 8
    UNION ALL SELECT 8, 6
    UNION ALL SELECT 8, 9
    UNION ALL SELECT 9, 8
    UNION ALL SELECT 9, 10
    UNION ALL SELECT 10, 9
    UNION ALL SELECT 11, 12
    UNION ALL SELECT 12, 11
    UNION ALL SELECT 11, 14
    UNION ALL SELECT 14, 11
    UNION ALL SELECT 12, 14
    UNION ALL SELECT 14, 12
    UNION ALL SELECT 13, 14
    UNION ALL SELECT 14, 13
    UNION ALL SELECT 14, 15
    UNION ALL SELECT 15, 14
    UNION ALL SELECT 15, 16
    UNION ALL SELECT 16, 15
    UNION ALL SELECT 17, 18
    UNION ALL SELECT 18, 17
    UNION ALL SELECT 18, 19
    UNION ALL SELECT 19, 18
    UNION ALL SELECT 20, 21
    UNION ALL SELECT 21, 20
    UNION ALL SELECT 2, 6
    UNION ALL SELECT 3, 8
    UNION ALL SELECT 4, 9
    UNION ALL SELECT 6, 11
    UNION ALL SELECT 8, 12
    UNION ALL SELECT 9, 14
    UNION ALL SELECT 10, 11
    UNION ALL SELECT 11, 17
    UNION ALL SELECT 12, 18
    UNION ALL SELECT 14, 17
    UNION ALL SELECT 15, 18
    UNION ALL SELECT 16, 17
    UNION ALL SELECT 17, 20
    UNION ALL SELECT 18, 20
    UNION ALL SELECT 20, 2
    UNION ALL SELECT 21, 3
    UNION ALL SELECT 4, 11
    UNION ALL SELECT 6, 9
    UNION ALL SELECT 8, 14
    UNION ALL SELECT 11, 20
    UNION ALL SELECT 20, 17
    UNION ALL SELECT 5, 8
    UNION ALL SELECT 7, 9
    UNION ALL SELECT 13, 18
    UNION ALL SELECT 16, 20
    UNION ALL SELECT 19, 20
) AS seed;

-- Pending requests target private profiles from UserService's seed set (users 5, 7, 10, 13, 16, 19).
-- Accepted requests have a matching follow edge; the remaining rows are request history.
INSERT IGNORE INTO follow_requests (sender_id, receiver_id, created_at, responded_at, status)
SELECT seed.sender_id,
       seed.receiver_id,
       DATE_FORMAT(
           CASE
               WHEN seed.status = 'ACCEPTED' THEN DATE_SUB(@relationship_seed_now, INTERVAL 21 DAY)
               ELSE DATE_SUB(@relationship_seed_now, INTERVAL 3 DAY)
           END,
           '%Y-%m-%d %H:%i:%s'
       ),
       CASE
           WHEN seed.status = 'PENDING' THEN NULL
           WHEN seed.status = 'ACCEPTED' THEN DATE_FORMAT(
               DATE_SUB(@relationship_seed_now, INTERVAL 20 DAY), '%Y-%m-%d %H:%i:%s')
           ELSE DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 1 DAY), '%Y-%m-%d %H:%i:%s')
       END,
       seed.status
FROM (
    SELECT 2 AS sender_id, 5 AS receiver_id, 'PENDING' AS status
    UNION ALL SELECT 3, 7, 'PENDING'
    UNION ALL SELECT 8, 10, 'PENDING'
    UNION ALL SELECT 9, 13, 'PENDING'
    UNION ALL SELECT 14, 16, 'PENDING'
    UNION ALL SELECT 17, 19, 'PENDING'
    UNION ALL SELECT 6, 5, 'ACCEPTED'
    UNION ALL SELECT 8, 7, 'ACCEPTED'
    UNION ALL SELECT 9, 10, 'ACCEPTED'
    UNION ALL SELECT 14, 13, 'ACCEPTED'
    UNION ALL SELECT 15, 16, 'ACCEPTED'
    UNION ALL SELECT 18, 19, 'ACCEPTED'
    UNION ALL SELECT 20, 5, 'REJECTED'
    UNION ALL SELECT 21, 7, 'CANCELLED'
    UNION ALL SELECT 2, 10, 'REJECTED'
) AS seed;

INSERT IGNORE INTO close_friends (owner_user_id, member_user_id, created_at)
SELECT seed.owner_user_id,
       seed.member_user_id,
       DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 10 DAY), '%Y-%m-%d %H:%i:%s')
FROM (
    SELECT 2 AS owner_user_id, 3 AS member_user_id
    UNION ALL SELECT 2, 4
    UNION ALL SELECT 3, 2
    UNION ALL SELECT 3, 4
    UNION ALL SELECT 4, 2
    UNION ALL SELECT 5, 6
    UNION ALL SELECT 6, 5
    UNION ALL SELECT 6, 8
    UNION ALL SELECT 7, 8
    UNION ALL SELECT 8, 7
    UNION ALL SELECT 9, 10
    UNION ALL SELECT 10, 9
    UNION ALL SELECT 11, 12
    UNION ALL SELECT 11, 14
    UNION ALL SELECT 12, 11
    UNION ALL SELECT 13, 14
    UNION ALL SELECT 14, 15
    UNION ALL SELECT 15, 16
    UNION ALL SELECT 17, 18
    UNION ALL SELECT 18, 19
    UNION ALL SELECT 20, 21
) AS seed;

-- Blocked pairs have no seeded follows, follow requests, or conversations between them.
INSERT IGNORE INTO blocks (blocker_id, blocked_id, created_at)
SELECT seed.blocker_id,
       seed.blocked_id,
       DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 5 DAY), '%Y-%m-%d %H:%i:%s')
FROM (
    SELECT 2 AS blocker_id, 19 AS blocked_id
    UNION ALL SELECT 10, 21
) AS seed;

-- Personal conversations only. Existing UserService IDs are used as participants.
INSERT IGNORE INTO conversations (
    created_by_id,
    direct_user_low_id,
    direct_user_high_id,
    last_message_id,
    name,
    created_at,
    updated_at,
    last_activity_at,
    `type`
)
SELECT seed.created_by_id,
       seed.user_low_id,
       seed.user_high_id,
       NULL,
       NULL,
       DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 30 DAY), '%Y-%m-%d %H:%i:%s'),
       DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 30 DAY), '%Y-%m-%d %H:%i:%s'),
       DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 30 DAY), '%Y-%m-%d %H:%i:%s'),
       'DIRECT'
FROM (
    SELECT 2 AS created_by_id, 2 AS user_low_id, 3 AS user_high_id
    UNION ALL SELECT 2, 2, 4
    UNION ALL SELECT 5, 5, 6
    UNION ALL SELECT 6, 6, 8
    UNION ALL SELECT 8, 8, 9
    UNION ALL SELECT 9, 9, 10
    UNION ALL SELECT 11, 11, 12
    UNION ALL SELECT 13, 13, 14
    UNION ALL SELECT 14, 14, 15
    UNION ALL SELECT 15, 15, 16
    UNION ALL SELECT 17, 17, 18
    UNION ALL SELECT 18, 18, 19
    UNION ALL SELECT 20, 20, 21
    UNION ALL SELECT 2, 2, 9
    UNION ALL SELECT 2, 2, 5
    UNION ALL SELECT 3, 3, 7
    UNION ALL SELECT 8, 8, 10
    UNION ALL SELECT 4, 4, 7
    UNION ALL SELECT 16, 16, 21
) AS seed;

-- Active conversations have both participants. Pending, rejected, and cancelled requests retain only the sender.
INSERT IGNORE INTO conversation_participants (conversation_id, user_id, `role`, joined_at, left_at)
SELECT conversation.id,
       seed.user_id,
       'MEMBER',
       DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 28 DAY), '%Y-%m-%d %H:%i:%s'),
       NULL
FROM (
    SELECT 2 AS user_low_id, 3 AS user_high_id, 2 AS user_id
    UNION ALL SELECT 2, 3, 3
    UNION ALL SELECT 2, 4, 2
    UNION ALL SELECT 2, 4, 4
    UNION ALL SELECT 5, 6, 5
    UNION ALL SELECT 5, 6, 6
    UNION ALL SELECT 6, 8, 6
    UNION ALL SELECT 6, 8, 8
    UNION ALL SELECT 8, 9, 8
    UNION ALL SELECT 8, 9, 9
    UNION ALL SELECT 9, 10, 9
    UNION ALL SELECT 9, 10, 10
    UNION ALL SELECT 11, 12, 11
    UNION ALL SELECT 11, 12, 12
    UNION ALL SELECT 13, 14, 13
    UNION ALL SELECT 13, 14, 14
    UNION ALL SELECT 14, 15, 14
    UNION ALL SELECT 14, 15, 15
    UNION ALL SELECT 15, 16, 15
    UNION ALL SELECT 15, 16, 16
    UNION ALL SELECT 17, 18, 17
    UNION ALL SELECT 17, 18, 18
    UNION ALL SELECT 18, 19, 18
    UNION ALL SELECT 18, 19, 19
    UNION ALL SELECT 20, 21, 20
    UNION ALL SELECT 20, 21, 21
    UNION ALL SELECT 2, 9, 2
    UNION ALL SELECT 2, 9, 9
    UNION ALL SELECT 2, 5, 2
    UNION ALL SELECT 3, 7, 3
    UNION ALL SELECT 8, 10, 8
    UNION ALL SELECT 4, 7, 4
    UNION ALL SELECT 16, 21, 16
) AS seed
JOIN conversations AS conversation
  ON conversation.`type` = 'DIRECT'
 AND conversation.direct_user_low_id = seed.user_low_id
 AND conversation.direct_user_high_id = seed.user_high_id;

-- Three pending requests, one accepted request, and two historical outcomes for the demo inbox.
INSERT IGNORE INTO conversation_requests (conversation_id, sender_id, receiver_id, created_at, responded_at, status)
SELECT conversation.id,
       seed.sender_id,
       seed.receiver_id,
       DATE_FORMAT(
           CASE
               WHEN seed.status = 'REJECTED' THEN DATE_SUB(@relationship_seed_now, INTERVAL 16 DAY)
               WHEN seed.status = 'CANCELLED' THEN DATE_SUB(@relationship_seed_now, INTERVAL 15 DAY)
               WHEN seed.status = 'PENDING' THEN DATE_SUB(@relationship_seed_now, INTERVAL 3 DAY)
               ELSE DATE_SUB(@relationship_seed_now, INTERVAL 2 DAY)
           END,
           '%Y-%m-%d %H:%i:%s'
       ),
       CASE
           WHEN seed.status = 'PENDING' THEN NULL
           WHEN seed.status = 'REJECTED' THEN DATE_FORMAT(
               DATE_SUB(@relationship_seed_now, INTERVAL 14 DAY), '%Y-%m-%d %H:%i:%s')
           WHEN seed.status = 'CANCELLED' THEN DATE_FORMAT(
               DATE_SUB(@relationship_seed_now, INTERVAL 13 DAY), '%Y-%m-%d %H:%i:%s')
           ELSE DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 1 DAY), '%Y-%m-%d %H:%i:%s')
       END,
       seed.status
FROM (
    SELECT 2 AS user_low_id, 5 AS user_high_id, 2 AS sender_id, 5 AS receiver_id, 'PENDING' AS status
    UNION ALL SELECT 3, 7, 3, 7, 'PENDING'
    UNION ALL SELECT 8, 10, 8, 10, 'PENDING'
    UNION ALL SELECT 2, 9, 2, 9, 'ACCEPTED'
    UNION ALL SELECT 4, 7, 4, 7, 'REJECTED'
    UNION ALL SELECT 16, 21, 16, 21, 'CANCELLED'
) AS seed
JOIN conversations AS conversation
  ON conversation.`type` = 'DIRECT'
 AND conversation.direct_user_low_id = seed.user_low_id
 AND conversation.direct_user_high_id = seed.user_high_id;

-- The payloads are readable demo text. The message API currently stores this field as opaque data;
-- this fixture does not claim that the sample messages are end-to-end encrypted.
INSERT IGNORE INTO messages (
    conversation_id,
    sender_id,
    reply_to_message_id,
    client_message_id,
    created_at,
    edited_at,
    deleted_at,
    `type`,
    ciphertext,
    status
)
SELECT conversation.id,
       seed.sender_id,
       NULL,
       seed.client_message_id,
       seed.created_at,
       seed.edited_at,
       seed.deleted_at,
       'TEXT',
       seed.ciphertext,
       seed.status
FROM (
    SELECT 2 AS user_low_id, 3 AS user_high_id, 2 AS sender_id,
           'd0000000-0000-4000-8000-000000000001' AS client_message_id,
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 12 DAY), '%Y-%m-%d %H:%i:%s') AS created_at,
           NULL AS edited_at, NULL AS deleted_at,
           'Cuối tuần này đi cà phê không?' AS ciphertext, 'SENT' AS status
    UNION ALL SELECT 2, 3, 3, 'd0000000-0000-4000-8000-000000000002',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 12 DAY) + INTERVAL 3 MINUTE, '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Được đó, mình rảnh chiều thứ bảy.', 'SENT'
    UNION ALL SELECT 2, 3, 2, 'd0000000-0000-4000-8000-000000000003',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 12 DAY) + INTERVAL 6 MINUTE, '%Y-%m-%d %H:%i:%s'),
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 12 DAY) + INTERVAL 7 MINUTE, '%Y-%m-%d %H:%i:%s'),
           NULL, 'Tớ gửi địa điểm sau nhé 🚀', 'EDITED'
    UNION ALL SELECT 2, 3, 3, 'd0000000-0000-4000-8000-000000000004',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 12 DAY) + INTERVAL 9 MINUTE, '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Ok, hẹn cậu!', 'SENT'

    UNION ALL SELECT 2, 4, 2, 'd0000000-0000-4000-8000-000000000005',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 10 DAY), '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Bạn xem bản thiết kế mới chưa?', 'SENT'
    UNION ALL SELECT 2, 4, 4, 'd0000000-0000-4000-8000-000000000006',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 10 DAY) + INTERVAL 5 MINUTE, '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Mình vừa xem rồi, ổn lắm!', 'SENT'

    UNION ALL SELECT 5, 6, 5, 'd0000000-0000-4000-8000-000000000007',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 9 DAY), '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Tối nay đi đá bóng không?', 'SENT'
    UNION ALL SELECT 5, 6, 6, 'd0000000-0000-4000-8000-000000000008',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 9 DAY) + INTERVAL 4 MINUTE, '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Có, 7 giờ sân cũ nhé ⚽', 'SENT'

    UNION ALL SELECT 6, 8, 6, 'd0000000-0000-4000-8000-000000000009',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 8 DAY), '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Mình gửi tài liệu API rồi.', 'SENT'
    UNION ALL SELECT 6, 8, 8, 'd0000000-0000-4000-8000-000000000010',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 8 DAY) + INTERVAL 2 MINUTE, '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Nhận được rồi, cảm ơn nhé.', 'SENT'
    UNION ALL SELECT 6, 8, 6, 'd0000000-0000-4000-8000-000000000011',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 8 DAY) + INTERVAL 4 MINUTE, '%Y-%m-%d %H:%i:%s'),
           NULL, DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 8 DAY) + INTERVAL 5 MINUTE, '%Y-%m-%d %H:%i:%s'),
           '', 'DELETED'

    UNION ALL SELECT 8, 9, 8, 'd0000000-0000-4000-8000-000000000012',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 7 DAY), '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Cậu đã chạy migration mới chưa?', 'SENT'
    UNION ALL SELECT 8, 9, 9, 'd0000000-0000-4000-8000-000000000013',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 7 DAY) + INTERVAL 7 MINUTE, '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Chạy xong rồi, mọi thứ ổn.', 'SENT'

    UNION ALL SELECT 9, 10, 9, 'd0000000-0000-4000-8000-000000000014',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 6 DAY), '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Bạn có rảnh trao đổi một chút không?', 'SENT'
    UNION ALL SELECT 9, 10, 10, 'd0000000-0000-4000-8000-000000000015',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 6 DAY) + INTERVAL 2 MINUTE, '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Được, bạn cứ nhắn nhé.', 'SENT'

    UNION ALL SELECT 11, 12, 11, 'd0000000-0000-4000-8000-000000000016',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 5 DAY), '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Bạn có quyển sách hôm trước không?', 'SENT'
    UNION ALL SELECT 11, 12, 12, 'd0000000-0000-4000-8000-000000000017',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 5 DAY) + INTERVAL 4 MINUTE, '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Có, mai mình mang theo.', 'SENT'

    UNION ALL SELECT 13, 14, 13, 'd0000000-0000-4000-8000-000000000018',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 4 DAY), '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Họp nhóm chuyển sang 3 giờ nhé.', 'SENT'
    UNION ALL SELECT 13, 14, 14, 'd0000000-0000-4000-8000-000000000019',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 4 DAY) + INTERVAL 3 MINUTE, '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Được, mình cập nhật lịch.', 'SENT'

    UNION ALL SELECT 14, 15, 14, 'd0000000-0000-4000-8000-000000000020',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 3 DAY), '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Ảnh chuyến đi đẹp quá!', 'SENT'
    UNION ALL SELECT 14, 15, 15, 'd0000000-0000-4000-8000-000000000021',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 3 DAY) + INTERVAL 3 MINUTE, '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Cảm ơn bạn 🌿', 'SENT'

    UNION ALL SELECT 15, 16, 15, 'd0000000-0000-4000-8000-000000000022',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 2 DAY), '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Mình gửi file rồi nhé.', 'SENT'
    UNION ALL SELECT 15, 16, 16, 'd0000000-0000-4000-8000-000000000023',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 2 DAY) + INTERVAL 3 MINUTE, '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Đã nhận, cảm ơn.', 'SENT'

    UNION ALL SELECT 17, 18, 17, 'd0000000-0000-4000-8000-000000000024',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 36 HOUR), '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Cuối tuần đi dã ngoại nhé!', 'SENT'
    UNION ALL SELECT 17, 18, 18, 'd0000000-0000-4000-8000-000000000025',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 36 HOUR) + INTERVAL 5 MINUTE, '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Mình tham gia.', 'SENT'

    UNION ALL SELECT 18, 19, 18, 'd0000000-0000-4000-8000-000000000026',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 24 HOUR), '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Mình gửi bạn vài tài liệu.', 'SENT'
    UNION ALL SELECT 18, 19, 19, 'd0000000-0000-4000-8000-000000000027',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 24 HOUR) + INTERVAL 3 MINUTE, '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Tuyệt, cảm ơn nhé.', 'SENT'

    UNION ALL SELECT 20, 21, 20, 'd0000000-0000-4000-8000-000000000028',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 18 HOUR), '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Dạo này thế nào?', 'SENT'
    UNION ALL SELECT 20, 21, 21, 'd0000000-0000-4000-8000-000000000029',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 18 HOUR) + INTERVAL 5 MINUTE, '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Mình ổn, còn bạn?', 'SENT'

    UNION ALL SELECT 2, 9, 2, 'd0000000-0000-4000-8000-000000000030',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 8 HOUR), '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Chào Nam, mình muốn hỏi về dự án.', 'SENT'
    UNION ALL SELECT 2, 9, 9, 'd0000000-0000-4000-8000-000000000031',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 8 HOUR) + INTERVAL 4 MINUTE, '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Chào bạn, cứ hỏi nhé.', 'SENT'
    UNION ALL SELECT 2, 9, 2, 'd0000000-0000-4000-8000-000000000032',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 8 HOUR) + INTERVAL 6 MINUTE, '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Cảm ơn bạn, mình gửi thông tin ngay.', 'SENT'

    UNION ALL SELECT 2, 5, 2, 'd0000000-0000-4000-8000-000000000033',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 2 DAY), '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Chào Đức, mình có thể hỏi bạn về Spring không?', 'SENT'
    UNION ALL SELECT 3, 7, 3, 'd0000000-0000-4000-8000-000000000034',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 30 HOUR), '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Chào Trí, mình muốn kết nối với bạn.', 'SENT'
    UNION ALL SELECT 8, 10, 8, 'd0000000-0000-4000-8000-000000000035',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 20 HOUR), '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Chào Khoa, mình gửi bạn thông tin này nhé.', 'SENT'
    UNION ALL SELECT 4, 7, 4, 'd0000000-0000-4000-8000-000000000036',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 15 DAY), '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Xin chào, chúng ta cùng học lập trình phải không?', 'SENT'
    UNION ALL SELECT 16, 21, 16, 'd0000000-0000-4000-8000-000000000037',
           DATE_FORMAT(DATE_SUB(@relationship_seed_now, INTERVAL 14 DAY), '%Y-%m-%d %H:%i:%s'),
           NULL, NULL, 'Chào Kiệt, mình gửi nhầm cuộc trò chuyện.', 'SENT'
) AS seed
JOIN conversations AS conversation
  ON conversation.`type` = 'DIRECT'
 AND conversation.direct_user_low_id = seed.user_low_id
 AND conversation.direct_user_high_id = seed.user_high_id;

-- Point seeded conversations to their latest seeded message and expose them at the right inbox position.
UPDATE conversations AS conversation
JOIN (
    SELECT message.conversation_id, MAX(message.id) AS last_message_id
    FROM messages AS message
    JOIN (
        SELECT candidate.conversation_id, MAX(candidate.created_at) AS last_created_at
        FROM messages AS candidate
        WHERE candidate.conversation_id IN (
            SELECT seeded_message.conversation_id
            FROM messages AS seeded_message
            WHERE seeded_message.client_message_id LIKE 'd0000000-%'
        )
        GROUP BY candidate.conversation_id
    ) AS latest_time
      ON latest_time.conversation_id = message.conversation_id
     AND latest_time.last_created_at = message.created_at
    GROUP BY message.conversation_id
) AS latest_message
  ON latest_message.conversation_id = conversation.id
JOIN messages AS last_message
  ON last_message.id = latest_message.last_message_id
SET conversation.last_message_id = last_message.id,
    conversation.updated_at = last_message.created_at,
    conversation.last_activity_at = last_message.created_at;

