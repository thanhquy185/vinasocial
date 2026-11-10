package vn.tuhoc.UserService.exceptions;

public class AuthAccessTokenIsNotValidException extends RuntimeException {
    public AuthAccessTokenIsNotValidException() {
        super("Access token không hợp lệ!");
    }
}