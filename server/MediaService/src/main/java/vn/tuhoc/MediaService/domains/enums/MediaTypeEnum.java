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
public enum MediaTypeEnum {
    IMAGE("IMAGE", "Ảnh"),
    VIDEO("VIDEO", "Video"),
    AUDIO("AUDIO", "Âm thanh"),
    FILE("FILE", "File");

    String value;
    String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static MediaTypeEnum fromDescription(String description) {
        for (MediaTypeEnum type : values()) {
            if (type.getDescription().equalsIgnoreCase(description)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + description);
    }

    public static MediaTypeEnum fromValue(String value) {
        for (MediaTypeEnum type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + value);
    }
}
