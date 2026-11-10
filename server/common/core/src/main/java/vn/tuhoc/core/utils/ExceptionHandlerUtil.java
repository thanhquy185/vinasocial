package vn.tuhoc.core.utils;

public class ExceptionHandlerUtil {
    public static String formatError(Exception exception) {
        return exception.getClass()
                .getSimpleName()
                .replace("Exception", "")
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toUpperCase();
    }
}
