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
public enum UsernameTypeEnum {
    TEXT("TEXT", "Văn bản"),
    PHONE("PHONE", "Số điện thoại"),
    EMAIL("EMAIL", "Email");

    String value;
    String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static UsernameTypeEnum fromDescription(String description) {
        for (UsernameTypeEnum type : values()) {
            if (type.getDescription().equalsIgnoreCase(description)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + description);
    }

    public static UsernameTypeEnum fromValue(String value) {
        for (UsernameTypeEnum type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + value);
    }
}
