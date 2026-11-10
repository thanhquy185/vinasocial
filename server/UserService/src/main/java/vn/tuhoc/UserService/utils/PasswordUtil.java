package vn.tuhoc.UserService.utils;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.exceptions.UserPasswordIsNotMatchException;
import vn.tuhoc.UserService.exceptions.UserPasswordIsUsingException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PasswordUtil {
    static PasswordEncoder passwordEncoder;

    PasswordUtil(PasswordEncoder passwordEncoder) {
        PasswordUtil.passwordEncoder = passwordEncoder;
    }

    public static void handlePasswordIsNotMatch(String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new UserPasswordIsNotMatchException(newPassword, confirmPassword);
        }
    }

    public static void handlePasswordIsUsing(String currentPassword, String newPassword) {
        if (passwordEncoder.matches(newPassword, currentPassword)) {
            throw new UserPasswordIsUsingException(newPassword);
        }
    }

    public static PasswordEncoder getPasswordEncoder() {
        return PasswordUtil.passwordEncoder;
    }
}
