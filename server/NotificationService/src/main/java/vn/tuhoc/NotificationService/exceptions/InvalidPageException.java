package vn.tuhoc.NotificationService.exceptions;

public class InvalidPageException extends RuntimeException {
    public InvalidPageException() {
        super("Trang phải từ 0 trở lên và số lượng mỗi trang từ 1 đến 50.");
    }
}
