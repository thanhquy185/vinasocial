package vn.tuhoc.UserService.exceptions;

public class ProfileLinkNotFoundByProfileIdException extends RuntimeException {
    public ProfileLinkNotFoundByProfileIdException(Integer profileId) {
        super(String.format("Thông tin đường dẫn có mã thông tin %s không tìm thấy!", profileId));
    }
}
