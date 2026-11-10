ALTER TABLE auth_otps
    DROP CHECK chk_auth_otps_purpose,
    ADD CONSTRAINT chk_auth_otps_purpose CHECK (
        purpose IN (
            'REGISTER', 'VERIFY_EMAIL', 'CHANGE_EMAIL', 'VERIFY_PHONE',
            'CHANGE_PHONE', 'LOGIN', 'RESET_PASSWORD', 'ENABLE_MFA',
            'DISABLE_MFA', 'CHANGE_PASS'
        )
    );
