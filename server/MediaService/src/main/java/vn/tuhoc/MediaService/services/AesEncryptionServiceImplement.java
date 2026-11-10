package vn.tuhoc.MediaService.services;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.MediaService.services.interfaces.AesEncryptionService;
import vn.tuhoc.MediaService.utils.EncryptionUtil;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AesEncryptionServiceImplement implements AesEncryptionService {
    private void validate(byte[] text, byte[] key, byte[] iv) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        if (key == null || key.length != 32) {
            throw new IllegalArgumentException("AES-256 key must be exactly 32 bytes");
        }
        if (iv == null || iv.length != EncryptionUtil.IV_SIZE) {
            throw new IllegalArgumentException("AES-GCM IV must be exactly 12 bytes");
        }
    }

    @Override
    public byte[] encrypt(byte[] plaintext, byte[] key, byte[] iv) {
        this.validate(plaintext, key, iv);

        try {
            SecretKey secretKey = new SecretKeySpec(key, EncryptionUtil.AES);

            GCMParameterSpec parameterSpec = new GCMParameterSpec(EncryptionUtil.TAG_LENGTH, iv);

            Cipher cipher = Cipher.getInstance(EncryptionUtil.AES_GCM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            return cipher.doFinal(plaintext);
        } catch (Exception e) {
            throw new IllegalStateException("AES-GCM encryption failed", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] ciphertext, byte[] key, byte[] iv) {
        this.validate(ciphertext, key, iv);

        try {
            SecretKey secretKey = new SecretKeySpec(key, EncryptionUtil.AES);

            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(EncryptionUtil.TAG_LENGTH, iv);

            Cipher cipher = Cipher.getInstance(EncryptionUtil.AES_GCM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

            return cipher.doFinal(ciphertext);
        } catch (Exception e) {
            throw new IllegalStateException("AES-GCM decryption failed", e);
        }
    }

}
