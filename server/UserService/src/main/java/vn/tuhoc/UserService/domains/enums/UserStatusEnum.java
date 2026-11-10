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
public enum UserStatusEnum {
    ACTIVE("ACTIVE", "Hoạt động"),
    INACTIVE("INACTIVE", "Tạm dừng"),
    FLAGGED("FLAGGED", "Bị báo cáo"),
    BANDED("BANDED", "Bị cấm");

    String value;
    String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static UserStatusEnum fromDescription(String description) {
        for (UserStatusEnum status : values()) {
            if (status.getDescription().equalsIgnoreCase(description)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + description);
    }

    public static UserStatusEnum fromValue(String value) {
        for (UserStatusEnum status : values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + value);
    }
}
