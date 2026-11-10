package vn.tuhoc.UserService.exceptions;

public class ProfileNotFoundByPhoneException extends RuntimeException {
    public ProfileNotFoundByPhoneException(String phone) {
        super(String.format("Thông tin có số điện thoại %s không tìm thấy!", phone));
    }
}
