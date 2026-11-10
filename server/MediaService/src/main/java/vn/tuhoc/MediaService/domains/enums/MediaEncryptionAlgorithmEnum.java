package vn.tuhoc.MediaService.domains.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum MediaEncryptionAlgorithmEnum {
    NONE("NONE", "Không mã hoá"),
    AES_256_GCM("AES_256_GCM", "AES-256-GCM"),
    RSA_OAEP_SHA256("RSA_OAEP_SHA256", "RSA-OAEP-SHA256");

    String value;
    String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static MediaEncryptionAlgorithmEnum fromDescription(String description) {
        for (MediaEncryptionAlgorithmEnum encryptionAlgorithm : values()) {
            if (encryptionAlgorithm.getDescription().equalsIgnoreCase(description)) {
                return encryptionAlgorithm;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + description);
    }

    public static MediaEncryptionAlgorithmEnum fromValue(String value) {
        for (MediaEncryptionAlgorithmEnum encryptionAlgorithm : values()) {
            if (encryptionAlgorithm.getValue().equalsIgnoreCase(value)) {
                return encryptionAlgorithm;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + value);
    }
}
