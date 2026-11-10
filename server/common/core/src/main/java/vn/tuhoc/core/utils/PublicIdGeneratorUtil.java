package vn.tuhoc.core.utils;

import java.security.SecureRandom;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicIdGeneratorUtil {
    static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    static final SecureRandom RANDOM = new SecureRandom();

    public static String generate(String prefix, Integer length) {
        StringBuilder result = new StringBuilder(String.format("%s-", prefix));

        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            result.append(CHARACTERS.charAt(index));
        }

        return result.toString();
    }
}
