package vn.tuhoc.UserService.exceptions;

public class ProfileNotFoundByUsernameException extends RuntimeException {
    public ProfileNotFoundByUsernameException(String username) {
        super(String.format("Thông tin có tên người dùng %s không tìm thấy!", username));
    }
}
