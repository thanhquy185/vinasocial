package vn.tuhoc.UserService.domains.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ProfilePrivacySettingPermissionEnum {
    EVERYONE("EVERYONE", "Tất cả"),
    BEST_FRIENDS("BEST_FRIENDS", "Bạn thân"),
    FOLLOWERS("FOLLOWERS", "Người theo dõi"),
    PRIVATE("PRIVATE", "Chỉ mình tôi");

    String value;
    String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static ProfilePrivacySettingPermissionEnum fromDescription(String description) {
        for (ProfilePrivacySettingPermissionEnum permission : values()) {
            if (permission.getDescription().equalsIgnoreCase(description)) {
                return permission;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + description);
    }

    public static ProfilePrivacySettingPermissionEnum fromValue(String value) {
        for (ProfilePrivacySettingPermissionEnum permission : values()) {
            if (permission.getValue().equalsIgnoreCase(value)) {
                return permission;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + value);
    }
}
