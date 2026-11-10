package vn.tuhoc.UserService.exceptions;

public class AuthOtpNotFoundByPublicIdException extends RuntimeException {
    public AuthOtpNotFoundByPublicIdException(String publicId) {
        super(String.format("Xác thực otp có mã %s không tìm thấy!", publicId));
    }
}
