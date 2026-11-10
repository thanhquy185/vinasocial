package vn.tuhoc.UserService.exceptions;

public class ProfileUsernameIsExistsException extends RuntimeException {
    public ProfileUsernameIsExistsException(String username) {
        super(String.format("Thông tin có tên người dùng %s đã tồn tại!", username));
    }
}
