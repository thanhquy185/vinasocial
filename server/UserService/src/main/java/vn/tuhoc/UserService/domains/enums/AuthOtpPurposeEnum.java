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
public enum AuthOtpPurposeEnum {
    REGISTER("REGISTER", "Đăng ký tài khoản"),
    LOGIN("LOGIN", "Đăng nhập tài khoản"),
    RESET_PASSWORD("RESET_PASSWORD", "Đặt lại mật khẩu"),
    CHANGE_PASSWORD("CHANGE_PASSWORD", "Đổi mật khẩu"),
    VERIFY_PHONE("VERIFY_PHONE", "Xác thực số điện thoại"),
    VERIFY_EMAIL("VERIFY_EMAIL", "Xác thực email"),
    CHANGE_PHONE("CHANGE_PHONE", "Thay đổi số điện thoại"),
    CHANGE_EMAIL("CHANGE_EMAIL", "Thay đổi email"),
    ENABLE_MFA("ENABLE_MFA", "Bật xác thực hai lớp"),
    DISABLE_MFA("DISABLE_MFA", "Tắt xác thực hai lớp");

    String value;
    String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static AuthOtpPurposeEnum fromDescription(String description) {
        for (AuthOtpPurposeEnum purpose : values()) {
            if (purpose.getDescription().equalsIgnoreCase(description)) {
                return purpose;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + description);
    }

    public static AuthOtpPurposeEnum fromValue(String value) {
        for (AuthOtpPurposeEnum purpose : values()) {
            if (purpose.getValue().equalsIgnoreCase(value)) {
                return purpose;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + value);
    }
}
