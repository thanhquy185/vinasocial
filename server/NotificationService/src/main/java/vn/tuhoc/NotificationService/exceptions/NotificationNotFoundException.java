package vn.tuhoc.NotificationService.exceptions;

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException() {
        super("Không tìm thấy thông báo.");
    }
}
