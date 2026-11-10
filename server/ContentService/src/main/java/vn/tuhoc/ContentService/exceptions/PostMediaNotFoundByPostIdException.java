package vn.tuhoc.ContentService.exceptions;

public class PostMediaNotFoundByPostIdException extends RuntimeException {
    public PostMediaNotFoundByPostIdException(Integer postId) {
        super(String.format("Phương tiện bài đăng có mã bài đăng %s không tìm thấy!", postId));
    }
}
