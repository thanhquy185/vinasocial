package vn.tuhoc.UserService.exceptions;

public class UserPasswordIsNotMatchException extends RuntimeException {
    public UserPasswordIsNotMatchException(String newPassword, String newPassword2) {
        super("Mật khẩu xác nhận không khớp.");
    }
}
