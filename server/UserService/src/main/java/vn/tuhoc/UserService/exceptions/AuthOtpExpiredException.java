package vn.tuhoc.UserService.exceptions;

public class AuthOtpExpiredException extends RuntimeException {
    public AuthOtpExpiredException() {
        super("Mã OTP đã hết hạn!");
    }
}
