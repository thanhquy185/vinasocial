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
public enum PostModerationStatusEnum {
    NONE("NONE", "Không"),
    FLAGGED("FLAGGED", "Đã báo cáo"),
    REJECTED("REJECTED", "Đã loại bỏ");

    String value;
    String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static PostModerationStatusEnum fromDescription(String description) {
        for (PostModerationStatusEnum moderationStatus : values()) {
            if (moderationStatus.getDescription().equalsIgnoreCase(description)) {
                return moderationStatus;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + description);
    }

    public static PostModerationStatusEnum fromValue(String value) {
        for (PostModerationStatusEnum moderationStatus : values()) {
            if (moderationStatus.getValue().equalsIgnoreCase(value)) {
                return moderationStatus;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + value);
    }
}
