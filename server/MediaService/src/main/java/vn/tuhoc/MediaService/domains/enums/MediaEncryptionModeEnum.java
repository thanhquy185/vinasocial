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
public enum MediaEncryptionModeEnum {
    NONE("NONE", "Không mã hoá"),
    SERVER_SIDE("SERVER_SIDE", "Mã hoá ở server"),
    E2EE("E2EE", "Mã hoá đầu cuối");

    String value;
    String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static MediaEncryptionModeEnum fromDescription(String description) {
        for (MediaEncryptionModeEnum encryptionMode : values()) {
            if (encryptionMode.getDescription().equalsIgnoreCase(description)) {
                return encryptionMode;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + description);
    }

    public static MediaEncryptionModeEnum fromValue(String value) {
        for (MediaEncryptionModeEnum encryptionMode : values()) {
            if (encryptionMode.getValue().equalsIgnoreCase(value)) {
                return encryptionMode;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + value);
    }
}
