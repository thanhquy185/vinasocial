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
public enum StoryModerationStatusEnum {
    REJECTED("REJECTED", "Vi phạm"),
    FLAGGED("FLAGGED", "Đang bị báo cáo"),
    NONE("NONE", "Bình thường");

    String value;
    String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static StoryModerationStatusEnum fromDescription(String description) {
        for (StoryModerationStatusEnum status : values()) {
            if (status.getDescription().equalsIgnoreCase(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy: " + description);
    }

    public static StoryModerationStatusEnum fromValue(String value) {
        for (StoryModerationStatusEnum status : values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy: " + value);
    }
}
