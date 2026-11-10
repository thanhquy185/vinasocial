# User Service

## OTP encryption key

OTP codes in `auth_otps.code` are encrypted with AES-256-GCM. Set a persistent,
random 32-byte key for the User Service before starting it:

```sh
export AUTH_OTP_ENCRYPTION_KEY_BASE64="$(openssl rand -base64 32)"
```

Keep the generated value in the deployment secret store and reuse it across
restarts and instances. Do not commit it or regenerate it after OTP rows have
been encrypted; losing it makes outstanding encrypted OTPs unverifiable.

For local development, the key is configured in `src/main/resources/application.yml`.
Keep the same value across restarts. For shared or production environments, store
the key in a secret manager and inject it as `AUTH_OTP_ENCRYPTION_KEY_BASE64`; do
not commit a production key.
