CREATE TABLE conversation_e2ee_device_keys (
    device_id CHAR(36) NOT NULL,
    user_id INT NOT NULL,
    public_key TEXT NOT NULL,
    created_at VARCHAR(19) NOT NULL,
    revoked_at VARCHAR(19) NULL,
    PRIMARY KEY (device_id),
    INDEX idx_conversation_e2ee_device_keys_user (user_id, revoked_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
