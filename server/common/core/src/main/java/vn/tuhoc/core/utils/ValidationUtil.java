package vn.tuhoc.core.utils;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public class ValidationUtil {
    public static boolean isNull(Object object) {
        return Objects.isNull(object);
    }

    public static boolean nonNull(Object object) {
        return Objects.nonNull(object);
    }

    public static boolean isNumeric(String string) {
        return StringUtils.isNumeric(string);
    }

    public static boolean hasText(String string) {
        return StringUtils.isNotBlank(StringUtils.trimToNull(string));
    }

    public static boolean optionalStringIsValid(Optional<String> optionalString) {
        return optionalString != null && optionalString.isPresent() && ValidationUtil.hasText(optionalString.get());
    }
}
