package vn.tuhoc.UserService.exceptions;

public class ProfileNotificationSettingNotFoundByProfileIdException extends RuntimeException {
    public ProfileNotificationSettingNotFoundByProfileIdException(Integer profileId) {
        super(String.format("Thông tin cài đặt thông báo có mã thông tin %s không tìm thấy!", profileId));
    }
}
