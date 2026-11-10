package vn.tuhoc.UserService.exceptions;

public class AuthOtpInvalidCodeException extends RuntimeException {
    public AuthOtpInvalidCodeException() {
        super("Mã OTP không chính xác!");
    }
}
