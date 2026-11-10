package vn.tuhoc.UserService.exceptions;

public class AuthOtpAlreadyVerifiedException extends RuntimeException {
    public AuthOtpAlreadyVerifiedException() {
        super("Mã OTP đã được xác thực!");
    }
}
