ALTER TABLE conversation_e2ee_device_keys
    ADD COLUMN device_name VARCHAR(120) NOT NULL DEFAULT 'Trình duyệt',
    ADD COLUMN last_seen_at VARCHAR(19) NULL;

UPDATE conversation_e2ee_device_keys
SET last_seen_at = created_at
WHERE last_seen_at IS NULL;

ALTER TABLE conversation_e2ee_device_keys
    MODIFY COLUMN last_seen_at VARCHAR(19) NOT NULL;
