CREATE TABLE conversation_message_device_key_wrappers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    message_id INT NOT NULL,
    device_id CHAR(36) NOT NULL,
    wrapped_key VARCHAR(512) NOT NULL,
    ciphertext_hash CHAR(64) NOT NULL,
    created_at VARCHAR(19) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_message_device_key_wrapper (message_id, device_id),
    INDEX idx_message_device_key_wrapper_device (device_id, message_id),
    CONSTRAINT fk_message_device_key_wrapper_message
        FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
    CONSTRAINT fk_message_device_key_wrapper_device
        FOREIGN KEY (device_id) REFERENCES conversation_e2ee_device_keys (device_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
