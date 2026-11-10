package vn.tuhoc.UserService.exceptions;

public class UserNotFoundByUsernameAndPasswordException extends RuntimeException {
    public UserNotFoundByUsernameAndPasswordException() {
        super("Tên tài khoản hoặc mật khẩu không đúng!");
    }
}
