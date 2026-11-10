package vn.tuhoc.UserService.services;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.services.interfaces.AuthOtpEncryptionService;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthOtpEncryptionServiceImplement implements AuthOtpEncryptionService {
    static String PREFIX = "v1.";
    static int IV_LENGTH = 12;
    static int TAG_LENGTH_BITS = 128;

    byte[] key;
    SecureRandom secureRandom = new SecureRandom();

    public AuthOtpEncryptionServiceImplement(
            @Value("${auth.otp.encryption-key-base64}") String encryptionKeyBase64) {
        if (encryptionKeyBase64 == null || encryptionKeyBase64.isBlank()) {
            throw new IllegalStateException(
                    "auth.otp.encryption-key-base64 is required; set a persistent Base64-encoded 32-byte key in configuration");
        }
        try {
            this.key = Base64.getDecoder().decode(encryptionKeyBase64);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("AUTH_OTP_ENCRYPTION_KEY_BASE64 must be valid Base64", exception);
        }
        if (this.key.length != 32) {
            throw new IllegalStateException("AUTH_OTP_ENCRYPTION_KEY_BASE64 must decode to exactly 32 bytes");
        }
    }

    @Override
    public String encrypt(String plaintext, String context) {
        byte[] iv = new byte[IV_LENGTH];
        this.secureRandom.nextBytes(iv);

        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(this.key, "AES"),
                    new GCMParameterSpec(TAG_LENGTH_BITS, iv)
            );
            cipher.updateAAD(context.getBytes(StandardCharsets.UTF_8));

            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

            return PREFIX + encoder.encodeToString(iv) + "." + encoder.encodeToString(encrypted);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Cannot encrypt authentication OTP", exception);
        }
    }

    @Override
    public String decrypt(String ciphertext, String context) {
        if (ciphertext == null || !ciphertext.startsWith(PREFIX)) {
            // Existing, short-lived OTP rows were stored in plaintext before this migration.
            return ciphertext;
        }

        String[] parts = ciphertext.split("\\.", 3);
        if (parts.length != 3) {
            throw new IllegalStateException("Stored authentication OTP has an invalid format");
        }

        try {
            Base64.Decoder decoder = Base64.getUrlDecoder();

            byte[] iv = decoder.decode(parts[1]);
            if (iv.length != IV_LENGTH) {
                throw new IllegalStateException("Stored authentication OTP has an invalid IV");
            }

            byte[] encrypted = decoder.decode(parts[2]);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(this.key, "AES"),
                    new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            cipher.updateAAD(context.getBytes(StandardCharsets.UTF_8));

            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException exception) {
            throw new IllegalStateException("Cannot decrypt authentication OTP", exception);
        }
    }
}
