package vn.tuhoc.NotificationService.exceptions;

public class InvalidNotificationIdException extends RuntimeException {
    public InvalidNotificationIdException() {
        super("Mã thông báo không hợp lệ.");
    }
}
