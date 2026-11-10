package vn.tuhoc.NotificationService.exceptions;

public class SmsOTPCantNotSendException extends RuntimeException {
    public SmsOTPCantNotSendException() {
        super("Không thể gửi SMS OTP!");
    }
}
