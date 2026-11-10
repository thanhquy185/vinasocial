package vn.tuhoc.NotificationService.exceptions;

public class EmailOTPCantNotSendException extends RuntimeException {
    public EmailOTPCantNotSendException() {
        super("Không thể gửi email OTP!");
    }
}
