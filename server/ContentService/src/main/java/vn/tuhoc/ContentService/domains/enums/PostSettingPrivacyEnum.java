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
public enum PostSettingPrivacyEnum {
    EVERYONE("EVERYONE", "Mọi người"),
    BEST_FRIENDS("BEST_FRIENDS", "Bạn thân"),
    FOLLOWERS("FOLLOWERS", "Người theo dõi"),
    NO_ONE("NO_ONE", "Chỉ mình tôi");

    String value;
    String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static PostSettingPrivacyEnum fromDescription(String description) {
        for (PostSettingPrivacyEnum privacy : values()) {
            if (privacy.getDescription().equalsIgnoreCase(description)) {
                return privacy;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + description);
    }

    public static PostSettingPrivacyEnum fromValue(String value) {
        for (PostSettingPrivacyEnum privacy : values()) {
            if (privacy.getValue().equalsIgnoreCase(value)) {
                return privacy;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + value);
    }
}
