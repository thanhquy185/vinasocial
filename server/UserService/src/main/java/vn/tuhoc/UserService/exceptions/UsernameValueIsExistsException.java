package vn.tuhoc.UserService.exceptions;

public class UsernameValueIsExistsException extends RuntimeException {
    public UsernameValueIsExistsException(String username) {
        super(String.format("Tên tài khoản %s đã tồn tại!", username));
    }
}
