package vn.tuhoc.UserService.exceptions;

public class ProfileNotFoundByIdException extends RuntimeException {
    public ProfileNotFoundByIdException(Integer id) {
        super(String.format("Thông tin có mã %s không tìm thấy!", id));
    }
}
