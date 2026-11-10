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
public enum MediaPurposeEnum {
    POST("POST", "Bài đăng"),
    REEL("REEL", "Thước phim"),
    STORY("STORY", "Tin"),
    HIGHLIGHT_STORY("HIGHLIGHT_STORY", "Tin nổi bật"),
    AVATAR("AVATAR", "Ảnh đại diện"),
    MESSAGE("MESSAGE", "Tin nhắn"),
    COMMENT("COMMENT", "Bình luận");

    String value;
    String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static MediaPurposeEnum fromDescription(String description) {
        for (MediaPurposeEnum purpose : values()) {
            if (purpose.getDescription().equalsIgnoreCase(description)) {
                return purpose;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + description);
    }

    public static MediaPurposeEnum fromValue(String value) {
        for (MediaPurposeEnum purpose : values()) {
            if (purpose.getValue().equalsIgnoreCase(value)) {
                return purpose;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + value);
    }
}
