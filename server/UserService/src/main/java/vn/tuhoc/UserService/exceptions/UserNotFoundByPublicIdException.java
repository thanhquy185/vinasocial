package vn.tuhoc.UserService.exceptions;

public class UserNotFoundByPublicIdException extends RuntimeException {
    public UserNotFoundByPublicIdException(String publicId) {
        super(String.format("Người dùng có mã công khai %s không tìm thấy!", publicId));
    }
}
