package vn.tuhoc.UserService.exceptions;

public class UsernameNotFoundByTypeAndValueException extends RuntimeException {
    public UsernameNotFoundByTypeAndValueException() {
        super("Không tìm thấy thông tin tên người dùng!");
    }
}
