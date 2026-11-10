package vn.tuhoc.UserService.exceptions;

public class UserNotFoundByUsernameException extends RuntimeException {
    public UserNotFoundByUsernameException(String username) {
        super(String.format("Người dùng có tên tài khoản %s không tìm thấy!", username));
    }
}
