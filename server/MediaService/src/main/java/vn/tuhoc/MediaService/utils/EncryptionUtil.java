package vn.tuhoc.MediaService.utils;

import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class EncryptionUtil {
    private static final SecureRandom secureRandom = new SecureRandom();
    /**
     * Algorithms
     */
    public static final String AES = "AES";
    public static final String AES_GCM = "AES/GCM/NoPadding";
    public static final String RSA = "RSA";
    public static final String RSA_OAEP = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    /**
     * SIZES
     */
    public static final int AES_KEY_SIZE = 256;
    public static final int RSA_KEY_SIZE = 3072;
    /**
     * GCM IV = 12 bytes
     */
    public static final int IV_SIZE = 12;
    /**
     * Authentication tag = 128 bits
     */
    public static final int TAG_LENGTH = 128;

    public static byte[] generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
            keyGenerator.init(AES_KEY_SIZE);

            SecretKey secretKey = keyGenerator.generateKey();

            return secretKey.getEncoded();

        } catch (Exception e) {
            throw new IllegalStateException("Cannot generate AES-256 key", e);
        }
    }

    public static byte[] generateIv() {
        byte[] iv = new byte[IV_SIZE];
        EncryptionUtil.secureRandom.nextBytes(iv);

        return iv;
    }
}
