package vn.tuhoc.UserService.exceptions;

public class ProfileSettingUpdateInvalidException extends RuntimeException {
    public ProfileSettingUpdateInvalidException() {
        super("Trường cài đặt hoặc giá trị cập nhật không hợp lệ.");
    }
}
