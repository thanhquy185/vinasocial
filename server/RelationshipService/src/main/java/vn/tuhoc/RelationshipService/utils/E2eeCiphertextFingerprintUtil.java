package vn.tuhoc.RelationshipService.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

public final class E2eeCiphertextFingerprintUtil {
    private E2eeCiphertextFingerprintUtil() {
    }

    public static String sha256Hex(String ciphertext) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(ciphertext.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception exception) {
            throw new IllegalStateException("Không thể tạo dấu kiểm tra ciphertext E2EE.", exception);
        }
    }
}
