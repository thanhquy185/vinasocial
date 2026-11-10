package vn.tuhoc.UserService.exceptions;

public class AuthSessionNotFoundByDeviceIdException extends RuntimeException {
    public AuthSessionNotFoundByDeviceIdException(String sessionDeviceId) {
        super(String.format("Phiên đăng nhập có mã thiết bị %s không tìm thấy!", sessionDeviceId));
    }
}
