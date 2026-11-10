package vn.tuhoc.RelationshipService.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Không tìm thấy tài khoản.");
    }
}
