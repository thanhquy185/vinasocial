package vn.tuhoc.MediaService.services.interfaces;

public interface AesEncryptionService {
    byte[] encrypt(byte[] plaintext, byte[] key, byte[] iv);

    byte[] decrypt(byte[] ciphertext, byte[] key, byte[] iv);
}
