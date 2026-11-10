package vn.tuhoc.UserService.exceptions;

public class ProfileNotFoundByUserIdException extends RuntimeException {
    public ProfileNotFoundByUserIdException(Integer userId) {
        super(String.format("Thông tin có mã người dùng %s không tìm thấy!", userId));
    }
}
