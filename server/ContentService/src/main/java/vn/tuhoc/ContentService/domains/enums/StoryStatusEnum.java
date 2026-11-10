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
public enum StoryStatusEnum {
    ACTIVE("ACTIVE", "Đang hiển thị"),
    HIDDEN("HIDDEN", "Đã ẩn"),
    EXPIRED("EXPIRED", "Hết hạn"),
    REMOVED("REMOVED", "Đã xoá");

    String value;
    String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static StoryStatusEnum fromDescription(String description) {
        for (StoryStatusEnum status : values()) {
            if (status.getDescription().equalsIgnoreCase(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy: " + description);
    }

    public static StoryStatusEnum fromValue(String value) {
        for (StoryStatusEnum status : values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy: " + value);
    }
}
