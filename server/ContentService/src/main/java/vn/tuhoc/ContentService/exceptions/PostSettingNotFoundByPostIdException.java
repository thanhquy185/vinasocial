package vn.tuhoc.ContentService.exceptions;

public class PostSettingNotFoundByPostIdException extends RuntimeException {
    public PostSettingNotFoundByPostIdException(Integer postId) {
        super(String.format("Cài đặt bài đăng có mã bài đăng %s không tìm thấy!", postId));
    }
}
