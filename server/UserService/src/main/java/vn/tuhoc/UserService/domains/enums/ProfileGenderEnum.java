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
public enum ProfileGenderEnum {
    MALE("MALE", "Nam"),
    FEMALE("FEMALE", "Nữ"),
    OTHER("OTHER", "Khác");

    String value;
    String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static ProfileGenderEnum fromDescription(String description) {
        for (ProfileGenderEnum gender : values()) {
            if (gender.getDescription().equalsIgnoreCase(description)) {
                return gender;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + description);
    }

    public static ProfileGenderEnum fromValue(String value) {
        for (ProfileGenderEnum gender : values()) {
            if (gender.getValue().equalsIgnoreCase(value)) {
                return gender;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + value);
    }
}
