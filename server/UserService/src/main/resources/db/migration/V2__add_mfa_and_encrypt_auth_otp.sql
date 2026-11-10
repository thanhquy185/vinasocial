ALTER TABLE users
    ADD COLUMN two_factor_enabled BIT(1) NOT NULL DEFAULT b'0';

ALTER TABLE auth_otps
    MODIFY COLUMN code VARCHAR(255) NULL,
    ADD COLUMN failed_attempts INT NOT NULL DEFAULT 0;

ALTER TABLE auth_otps
    DROP CHECK auth_otps_chk_1;

ALTER TABLE auth_otps
    ADD CONSTRAINT chk_auth_otps_purpose CHECK (
        purpose IN (
            'REGISTER', 'VERIFY_EMAIL', 'CHANGE_EMAIL', 'VERIFY_PHONE',
            'CHANGE_PHONE', 'LOGIN', 'RESET_PASSWORD', 'ENABLE_MFA', 'DISABLE_MFA'
        )
    );
