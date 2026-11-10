package vn.tuhoc.ContentService.domains.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum PostStatusEnum {
    PUBLICLY("PUBLICLY", "Đang công khai"),
    HIDDEN("HIDDEN", "Đã ẩn"),
    REMOVED("REMOVED", "Đã xoá");

    String value;
    String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static PostStatusEnum fromDescription(String description) {
        for (PostStatusEnum status : values()) {
            if (status.getDescription().equalsIgnoreCase(description)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + description);
    }

    public static PostStatusEnum fromValue(String value) {
        for (PostStatusEnum status : values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + value);
    }
}
