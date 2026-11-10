package vn.tuhoc.RelationshipService.exceptions;

public class UserServiceUnavailableException extends RuntimeException {
    public UserServiceUnavailableException() {
        super("Không thể xác minh tài khoản đích lúc này.");
    }
}
