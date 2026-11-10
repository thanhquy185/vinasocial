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
public enum MediaEncryptionKeyTypeEnum {
    SERVER("SERVER", "Server"),
    USER("USER", "User");

    String value;
    String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static MediaEncryptionKeyTypeEnum fromDescription(String description) {
        for (MediaEncryptionKeyTypeEnum encryptionKeyType : values()) {
            if (encryptionKeyType.getDescription().equalsIgnoreCase(description)) {
                return encryptionKeyType;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + description);
    }

    public static MediaEncryptionKeyTypeEnum fromValue(String value) {
        for (MediaEncryptionKeyTypeEnum encryptionKeyType : values()) {
            if (encryptionKeyType.getValue().equalsIgnoreCase(value)) {
                return encryptionKeyType;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + value);
    }
}
