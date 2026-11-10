ALTER TABLE conversation_e2ee_device_keys
    ADD COLUMN status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    ADD COLUMN auth_session_device_id VARCHAR(36) NULL,
    ADD COLUMN pairing_code_mac VARCHAR(64) NULL,
    ADD COLUMN pairing_expires_at VARCHAR(19) NULL,
    ADD COLUMN pairing_failed_attempts INT NOT NULL DEFAULT 0,
    ADD INDEX idx_conversation_e2ee_device_pairing (user_id, status, revoked_at);

UPDATE conversation_e2ee_device_keys
SET status = 'REVOKED'
WHERE revoked_at IS NOT NULL;
