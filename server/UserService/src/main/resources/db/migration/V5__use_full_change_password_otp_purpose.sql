ALTER TABLE auth_otps
    DROP CHECK chk_auth_otps_purpose,
    MODIFY COLUMN purpose VARCHAR(32) NOT NULL;

UPDATE auth_otps
SET purpose = 'CHANGE_PASSWORD'
WHERE purpose = 'CHANGE_PASS';

ALTER TABLE auth_otps
    ADD CONSTRAINT chk_auth_otps_purpose CHECK (
        purpose IN (
            'REGISTER', 'VERIFY_EMAIL', 'CHANGE_EMAIL', 'VERIFY_PHONE',
            'CHANGE_PHONE', 'LOGIN', 'RESET_PASSWORD', 'ENABLE_MFA',
            'DISABLE_MFA', 'CHANGE_PASSWORD'
        )
    );
