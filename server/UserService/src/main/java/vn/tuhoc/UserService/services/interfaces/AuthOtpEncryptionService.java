package vn.tuhoc.UserService.services.interfaces;

public interface AuthOtpEncryptionService {
    String encrypt(String plaintext, String context);

    String decrypt(String ciphertext, String context);
}
