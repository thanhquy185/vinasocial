CREATE TABLE follows (
    id INT NOT NULL AUTO_INCREMENT,
    follower_id INT NOT NULL,
    following_id INT NOT NULL,
    created_at VARCHAR(19) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_follows_pair UNIQUE (follower_id, following_id),
    CONSTRAINT chk_follows_not_self CHECK (follower_id <> following_id),
    INDEX idx_follows_following_created (following_id, created_at),
    INDEX idx_follows_follower_created (follower_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE follow_requests (
    id INT NOT NULL AUTO_INCREMENT,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    created_at VARCHAR(19) NOT NULL,
    responded_at VARCHAR(19) NULL,
    status VARCHAR(10) NOT NULL DEFAULT 'PENDING',
    PRIMARY KEY (id),
    CONSTRAINT uk_follow_requests_pair UNIQUE (sender_id, receiver_id),
    CONSTRAINT chk_follow_requests_not_self CHECK (sender_id <> receiver_id),
    CONSTRAINT chk_follow_requests_status CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED')),
    INDEX idx_follow_requests_receiver_status (receiver_id, status, created_at),
    INDEX idx_follow_requests_sender_status (sender_id, status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE blocks (
    id INT NOT NULL AUTO_INCREMENT,
    blocker_id INT NOT NULL,
    blocked_id INT NOT NULL,
    created_at VARCHAR(19) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_blocks_pair UNIQUE (blocker_id, blocked_id),
    CONSTRAINT chk_blocks_not_self CHECK (blocker_id <> blocked_id),
    INDEX idx_blocks_blocked (blocked_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE close_friends (
    id INT NOT NULL AUTO_INCREMENT,
    owner_user_id INT NOT NULL,
    member_user_id INT NOT NULL,
    created_at VARCHAR(19) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_close_friends_pair UNIQUE (owner_user_id, member_user_id),
    CONSTRAINT chk_close_friends_not_self CHECK (owner_user_id <> member_user_id),
    INDEX idx_close_friends_owner_created (owner_user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
