package vn.tuhoc.MediaService.services.interfaces;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface RsaEncryptionService {
    PublicKey parsePublicKey(String base64PublicKey);

    PrivateKey parsePrivateKey(String base64PrivateKey);

    byte[] encrypt(byte[] data, PublicKey publicKey);

    byte[] decrypt(byte[] encryptedData, PrivateKey privateKey);
}
