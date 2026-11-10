package vn.tuhoc.UserService.exceptions;

public class ProfilePropertyAlreadyExistsException extends RuntimeException {
    public ProfilePropertyAlreadyExistsException(String property) {
        super(String.format("%s đã được sử dụng bởi tài khoản khác.", property));
    }
}
