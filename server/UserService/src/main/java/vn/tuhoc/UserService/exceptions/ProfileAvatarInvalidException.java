package vn.tuhoc.UserService.exceptions;

public class ProfileAvatarInvalidException extends RuntimeException {
    public ProfileAvatarInvalidException() {
        super("Ảnh đại diện không hợp lệ hoặc không thuộc tài khoản của bạn.");
    }
}
