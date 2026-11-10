CREATE TABLE conversations (
    id INT NOT NULL AUTO_INCREMENT,
    created_by_id INT NOT NULL,
    direct_user_low_id INT NULL,
    direct_user_high_id INT NULL,
    last_message_id INT NULL,
    name VARCHAR(100) NULL,
    created_at VARCHAR(19) NOT NULL,
    updated_at VARCHAR(19) NOT NULL,
    last_activity_at VARCHAR(19) NOT NULL,
    `type` VARCHAR(8) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_conversations_direct_pair UNIQUE (direct_user_low_id, direct_user_high_id),
    CONSTRAINT chk_conversations_type CHECK (
        (`type` = 'DIRECT' AND direct_user_low_id IS NOT NULL AND direct_user_high_id IS NOT NULL AND direct_user_low_id < direct_user_high_id)
        OR (`type` = 'GROUP' AND direct_user_low_id IS NULL AND direct_user_high_id IS NULL)
    ),
    INDEX idx_conversations_created_by (created_by_id),
    INDEX idx_conversations_activity (last_activity_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE conversation_participants (
    id INT NOT NULL AUTO_INCREMENT,
    conversation_id INT NOT NULL,
    user_id INT NOT NULL,
    `role` VARCHAR(6) NOT NULL DEFAULT 'MEMBER',
    joined_at VARCHAR(19) NOT NULL,
    left_at VARCHAR(19) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_conversation_participants_pair UNIQUE (conversation_id, user_id),
    CONSTRAINT chk_conversation_participant_role CHECK (`role` IN ('OWNER', 'ADMIN', 'MEMBER')),
    CONSTRAINT fk_conversation_participants_conversation FOREIGN KEY (conversation_id)
        REFERENCES conversations(id) ON DELETE CASCADE,
    INDEX idx_conversation_participants_user (user_id, left_at, conversation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE conversation_requests (
    id INT NOT NULL AUTO_INCREMENT,
    conversation_id INT NOT NULL,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    created_at VARCHAR(19) NOT NULL,
    responded_at VARCHAR(19) NULL,
    status VARCHAR(10) NOT NULL DEFAULT 'PENDING',
    PRIMARY KEY (id),
    CONSTRAINT uk_conversation_requests_conversation UNIQUE (conversation_id),
    CONSTRAINT chk_conversation_requests_not_self CHECK (sender_id <> receiver_id),
    CONSTRAINT chk_conversation_requests_status CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED')),
    CONSTRAINT fk_conversation_requests_conversation FOREIGN KEY (conversation_id)
        REFERENCES conversations(id) ON DELETE CASCADE,
    INDEX idx_conversation_requests_receiver_status (receiver_id, status, created_at),
    INDEX idx_conversation_requests_sender_status (sender_id, status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE messages (
    id INT NOT NULL AUTO_INCREMENT,
    conversation_id INT NOT NULL,
    sender_id INT NOT NULL,
    reply_to_message_id INT NULL,
    client_message_id CHAR(36) NOT NULL,
    created_at VARCHAR(19) NOT NULL,
    edited_at VARCHAR(19) NULL,
    deleted_at VARCHAR(19) NULL,
    `type` VARCHAR(8) NOT NULL DEFAULT 'TEXT',
    ciphertext MEDIUMTEXT NOT NULL,
    status VARCHAR(8) NOT NULL DEFAULT 'SENT',
    PRIMARY KEY (id),
    CONSTRAINT uk_messages_sender_client_id UNIQUE (sender_id, client_message_id),
    CONSTRAINT chk_messages_type CHECK (`type` IN ('TEXT', 'IMAGE', 'VIDEO', 'AUDIO', 'FILE', 'STICKER')),
    CONSTRAINT chk_messages_status CHECK (status IN ('SENT', 'EDITED', 'DELETED')),
    CONSTRAINT fk_messages_conversation FOREIGN KEY (conversation_id)
        REFERENCES conversations(id) ON DELETE CASCADE,
    CONSTRAINT fk_messages_reply_to FOREIGN KEY (reply_to_message_id)
        REFERENCES messages(id) ON DELETE SET NULL,
    INDEX idx_messages_conversation_id (conversation_id, id),
    INDEX idx_messages_sender_created (sender_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
