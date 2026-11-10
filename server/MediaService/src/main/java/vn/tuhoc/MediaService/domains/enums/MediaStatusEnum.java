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
public enum MediaStatusEnum {
    UPLOADING("UPLOADING", "Đang tải"),
    FAILED("FAILED", "Tải thất bại"),
    SUCCEED("SUCCEED", "Tải thành công");

    String value;
    String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static MediaStatusEnum fromDescription(String description) {
        for (MediaStatusEnum status : values()) {
            if (status.getDescription().equalsIgnoreCase(description)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + description);
    }

    public static MediaStatusEnum fromValue(String value) {
        for (MediaStatusEnum status : values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + value);
    }
}
