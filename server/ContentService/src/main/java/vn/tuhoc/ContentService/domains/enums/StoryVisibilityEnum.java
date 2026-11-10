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
public enum StoryVisibilityEnum {
    EVERYONE("EVERYONE", "Mọi người"),
    BEST_FRIENDS("BEST_FRIENDS", "Bạn thân"),
    FRIENDS("FRIENDS", "Bạn bè"),
    FOLLOWERS("FOLLOWERS", "Người theo dõi"),
    NO_ONE("NO_ONE", "Chỉ mình tôi");

    String value;
    String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static StoryVisibilityEnum fromDescription(String description) {
        for (StoryVisibilityEnum visibility : values()) {
            if (visibility.getDescription().equalsIgnoreCase(description)) {
                return visibility;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy: " + description);
    }

    public static StoryVisibilityEnum fromValue(String value) {
        for (StoryVisibilityEnum visibility : values()) {
            if (visibility.getValue().equalsIgnoreCase(value)) {
                return visibility;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy: " + value);
    }
}
