package vn.tuhoc.UserService.exceptions;

public class ProfileNotFoundByEmailException extends RuntimeException {
    public ProfileNotFoundByEmailException(String email) {
        super(String.format("Thông tin có email %s không tìm thấy!", email));
    }
}
