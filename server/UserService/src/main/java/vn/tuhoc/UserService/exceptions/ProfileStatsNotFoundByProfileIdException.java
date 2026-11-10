package vn.tuhoc.UserService.exceptions;

public class ProfileStatsNotFoundByProfileIdException extends RuntimeException {
    public ProfileStatsNotFoundByProfileIdException(Integer profileId) {
        super(String.format("Thông tin thống kê có mã thông tin %s không tìm thấy!", profileId));
    }
}
