package vn.tuhoc.core.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeUtil {
    static final String DATE_FORMAT = "yyyy-MM-dd";
    static final String TIME_FORMAT = "HH:mm:ss";
    static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static LocalDateTime getLocalDateTime(String datetime) {
        return LocalDateTime.parse(
                datetime,
                DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }

    public static String getDate(LocalDateTime localDateTime) {
        return localDateTime
                .format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    public static String getTime(LocalDateTime localDateTime) {
        return localDateTime
                .format(DateTimeFormatter.ofPattern(TIME_FORMAT));
    }

    public static String getDatetime(LocalDateTime localDateTime) {
        return localDateTime
                .format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }

    public static String getCurrentDate() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    public static String getCurrentTime() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(TIME_FORMAT));
    }

    public static String getCurrentDatetime() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }
}