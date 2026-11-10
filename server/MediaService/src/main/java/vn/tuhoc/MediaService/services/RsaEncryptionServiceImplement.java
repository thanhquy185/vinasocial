package vn.tuhoc.MediaService.services;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.MediaService.services.interfaces.RsaEncryptionService;
import vn.tuhoc.MediaService.utils.EncryptionUtil;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RsaEncryptionServiceImplement implements RsaEncryptionService {
    @Override
    public PublicKey parsePublicKey(String base64PublicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

            KeyFactory keyFactory = KeyFactory.getInstance(EncryptionUtil.RSA);

            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid RSA public key", e);
        }
    }

    @Override
    public PrivateKey parsePrivateKey(String base64PrivateKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

            KeyFactory keyFactory = KeyFactory.getInstance(EncryptionUtil.RSA);

            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid RSA private key", e);
        }
    }

    @Override
    public byte[] encrypt(byte[] data, PublicKey publicKey) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if (publicKey == null) {
            throw new IllegalArgumentException("Public key cannot be null");
        }

        try {
            Cipher cipher = Cipher.getInstance(EncryptionUtil.RSA_OAEP);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            return cipher.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("RSA encryption failed", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] encryptedData, PrivateKey privateKey) {
        if (encryptedData == null) {
            throw new IllegalArgumentException("Encrypted data cannot be null");
        }
        if (privateKey == null) {
            throw new IllegalArgumentException("Private key cannot be null");
        }

        try {
            Cipher cipher = Cipher.getInstance(EncryptionUtil.RSA_OAEP);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            return cipher.doFinal(encryptedData);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("RSA decryption failed", e);
        }
    }
}
