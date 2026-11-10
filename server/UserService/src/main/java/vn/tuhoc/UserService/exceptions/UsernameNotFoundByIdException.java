package vn.tuhoc.UserService.exceptions;

public class UsernameNotFoundByIdException extends RuntimeException {
    public UsernameNotFoundByIdException(Integer id) {
        super(String.format("Tên tài khoản có mã %s không tìm thấy!", id));
    }
}
