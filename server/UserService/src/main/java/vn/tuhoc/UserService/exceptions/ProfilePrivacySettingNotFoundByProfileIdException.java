package vn.tuhoc.UserService.exceptions;

public class ProfilePrivacySettingNotFoundByProfileIdException extends RuntimeException {
    public ProfilePrivacySettingNotFoundByProfileIdException(Integer profileId) {
        super(String.format("Thông tin cài đặt quyền riêng tư có mã thông tin %s không tìm thấy!", profileId));
    }
}
