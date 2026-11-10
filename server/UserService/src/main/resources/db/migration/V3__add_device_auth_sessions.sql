ALTER TABLE auth_sessions
    ADD COLUMN device_installation_id VARCHAR(36) NULL,
    ADD COLUMN session_device_id VARCHAR(36) NULL,
    ADD COLUMN device_name VARCHAR(120) NULL,
    ADD COLUMN last_seen_at VARCHAR(19) NULL,
    ADD COLUMN e2ee_device_id VARCHAR(64) NULL;

UPDATE auth_sessions
SET device_installation_id = UUID(),
    session_device_id = UUID(),
    device_name = 'Trình duyệt hiện tại',
    last_seen_at = created_at
WHERE device_installation_id IS NULL
   OR session_device_id IS NULL
   OR device_name IS NULL
   OR last_seen_at IS NULL;

ALTER TABLE auth_sessions
    MODIFY COLUMN device_installation_id VARCHAR(36) NOT NULL,
    MODIFY COLUMN session_device_id VARCHAR(36) NOT NULL,
    MODIFY COLUMN device_name VARCHAR(120) NOT NULL,
    MODIFY COLUMN last_seen_at VARCHAR(19) NOT NULL,
    ADD CONSTRAINT uk_auth_sessions_session_device_id UNIQUE (session_device_id),
    ADD INDEX idx_auth_sessions_user_installation_active (user_id, device_installation_id, revoked_at),
    ADD INDEX idx_auth_sessions_user_last_seen (user_id, last_seen_at);
