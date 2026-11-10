ALTER TABLE conversation_e2ee_device_keys
    ADD COLUMN rebind_challenge_mac VARCHAR(64) NULL,
    ADD COLUMN rebind_expires_at VARCHAR(19) NULL,
    ADD COLUMN rebind_session_device_id VARCHAR(36) NULL;
