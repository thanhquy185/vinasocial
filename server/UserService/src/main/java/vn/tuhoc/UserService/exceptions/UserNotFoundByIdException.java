package vn.tuhoc.UserService.exceptions;

public class UserNotFoundByIdException extends RuntimeException {
    public UserNotFoundByIdException(Integer id) {
        super(String.format("Người dùng có mã %s không tìm thấy!", id));
    }
}
