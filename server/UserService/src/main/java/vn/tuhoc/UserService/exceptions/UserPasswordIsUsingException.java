package vn.tuhoc.UserService.exceptions;

public class UserPasswordIsUsingException extends RuntimeException {
    public UserPasswordIsUsingException(String newPassword) {
        super("Mật khẩu mới phải khác mật khẩu hiện tại.");
    }
}
