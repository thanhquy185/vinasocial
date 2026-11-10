package vn.tuhoc.UserService.exceptions;

public class AuthOtpIsNotVerifiedException extends RuntimeException {
    public AuthOtpIsNotVerifiedException() {
        super("Mã OTP chưa được xác thực!");
    }
}
