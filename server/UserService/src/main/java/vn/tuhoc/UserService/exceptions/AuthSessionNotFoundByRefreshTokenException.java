package vn.tuhoc.UserService.exceptions;

public class AuthSessionNotFoundByRefreshTokenException extends RuntimeException {
    public AuthSessionNotFoundByRefreshTokenException() {
        super("Mã refresh token chưa bị thu hồi của người dùng không tìm thấy!");
    }
}