package vn.tuhoc.ContentService.exceptions;

public class PostStatsNotFoundByPostIdException extends RuntimeException {
    public PostStatsNotFoundByPostIdException(Integer postId) {
        super(String.format("Thống kê bài đăng có mã bài đăng %s không tìm thấy!", postId));
    }
}
