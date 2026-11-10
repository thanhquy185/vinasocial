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
public enum PostTypeEnum {
    MEDIA("MEDIA", "Ảnh / Video"),
    REEL("REEL", "Thước phim"),
    FEELING("FEELING", "Cảm xúc");

    String value;
    String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static PostTypeEnum fromDescription(String description) {
        for (PostTypeEnum type : values()) {
            if (type.getDescription().equalsIgnoreCase(description)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + description);
    }

    public static PostTypeEnum fromValue(String value) {
        for (PostTypeEnum type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + value);
    }
}
