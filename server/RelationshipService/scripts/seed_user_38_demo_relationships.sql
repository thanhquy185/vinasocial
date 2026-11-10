-- Run against the local MySQL instance after UserService and RelationshipService migrations.
-- User 38 is the existing demo account; users 2-21 are the seeded profiles.
-- Safe to rerun: existing relationships are preserved and profile counters are recalculated.

START TRANSACTION;

INSERT IGNORE INTO vinasocial_relationship.follows (follower_id, following_id, created_at)
SELECT edges.follower_id, edges.following_id,
       DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 2 DAY), '%Y-%m-%d %H:%i:%s')
FROM (
    SELECT 38 AS follower_id, 2 AS following_id
    UNION ALL SELECT 2, 38
    UNION ALL SELECT 38, 3
    UNION ALL SELECT 3, 38
    UNION ALL SELECT 38, 8
    UNION ALL SELECT 8, 38
    UNION ALL SELECT 38, 9
    UNION ALL SELECT 9, 38
    UNION ALL SELECT 38, 11
    UNION ALL SELECT 11, 38
    UNION ALL SELECT 38, 14
    UNION ALL SELECT 14, 38
    UNION ALL SELECT 38, 4
    UNION ALL SELECT 38, 6
    UNION ALL SELECT 38, 12
    UNION ALL SELECT 38, 15
    UNION ALL SELECT 38, 17
    UNION ALL SELECT 38, 20
    UNION ALL SELECT 5, 38
    UNION ALL SELECT 7, 38
    UNION ALL SELECT 10, 38
    UNION ALL SELECT 13, 38
    UNION ALL SELECT 16, 38
    UNION ALL SELECT 18, 38
    UNION ALL SELECT 19, 38
    UNION ALL SELECT 21, 38
) AS edges
JOIN vinasocial_user.users AS follower ON follower.id = edges.follower_id
JOIN vinasocial_user.users AS followed ON followed.id = edges.following_id
WHERE NOT EXISTS (
    SELECT 1 FROM vinasocial_relationship.blocks AS b
    WHERE (b.blocker_id = edges.follower_id AND b.blocked_id = edges.following_id)
       OR (b.blocker_id = edges.following_id AND b.blocked_id = edges.follower_id)
);

-- These targets are private profiles. The demo user has requested, but does not yet follow, them.
INSERT IGNORE INTO vinasocial_relationship.follow_requests
    (sender_id, receiver_id, created_at, responded_at, status)
SELECT 38, target.id,
       DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 1 DAY), '%Y-%m-%d %H:%i:%s'),
       NULL, 'PENDING'
FROM vinasocial_user.users AS demo
JOIN vinasocial_user.users AS target ON target.id IN (5, 10)
WHERE demo.id = 38
  AND NOT EXISTS (
      SELECT 1 FROM vinasocial_relationship.follows AS f
      WHERE f.follower_id = 38 AND f.following_id = target.id
  )
  AND NOT EXISTS (
      SELECT 1 FROM vinasocial_relationship.blocks AS b
      WHERE (b.blocker_id = 38 AND b.blocked_id = target.id)
         OR (b.blocker_id = target.id AND b.blocked_id = 38)
  );

INSERT IGNORE INTO vinasocial_relationship.close_friends
    (owner_user_id, member_user_id, created_at)
SELECT pair.owner_user_id, pair.member_user_id,
       DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 1 DAY), '%Y-%m-%d %H:%i:%s')
FROM (
    SELECT 38 AS owner_user_id, 2 AS member_user_id
    UNION ALL SELECT 38, 8
    UNION ALL SELECT 38, 11
    UNION ALL SELECT 2, 38
) AS pair
JOIN vinasocial_relationship.follows AS outbound
  ON outbound.follower_id = pair.owner_user_id
 AND outbound.following_id = pair.member_user_id
JOIN vinasocial_relationship.follows AS inbound
  ON inbound.follower_id = pair.member_user_id
 AND inbound.following_id = pair.owner_user_id;

-- Profile stats live in UserService; update only the demo user and its selected peers.
UPDATE vinasocial_user.profile_stats AS stats
JOIN vinasocial_user.profiles AS profile ON profile.id = stats.profile_id
SET stats.follower_count = (
        SELECT COUNT(*) FROM vinasocial_relationship.follows AS f
        WHERE f.following_id = profile.user_id
    ),
    stats.following_count = (
        SELECT COUNT(*) FROM vinasocial_relationship.follows AS f
        WHERE f.follower_id = profile.user_id
    ),
    stats.friend_count = (
        SELECT COUNT(*)
        FROM vinasocial_relationship.follows AS outbound
        JOIN vinasocial_relationship.follows AS inbound
          ON inbound.follower_id = outbound.following_id
         AND inbound.following_id = outbound.follower_id
        WHERE outbound.follower_id = profile.user_id
    ),
    stats.best_friend_count = (
        SELECT COUNT(*) FROM vinasocial_relationship.close_friends AS cf
        WHERE cf.owner_user_id = profile.user_id
    )
WHERE profile.user_id IN (38, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21);

COMMIT;
