package vn.tuhoc.UserService.utils;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.core.utils.ValidationUtil;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentifierUtil {
    static Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    static Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[0-9]{9,15}$");

    public static boolean isEmail(String identifier) {
        if (ValidationUtil.isNull(identifier) || !ValidationUtil.hasText(identifier)) {
            return false;
        }

        return EMAIL_PATTERN.matcher(identifier.trim()).matches();
    }

    public static boolean isPhone(String identifier) {
        if (ValidationUtil.isNull(identifier) || !ValidationUtil.hasText(identifier)) {
            return false;
        }

        return PHONE_PATTERN.matcher(identifier.trim()).matches();
    }
}
