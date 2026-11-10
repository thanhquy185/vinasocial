package vn.tuhoc.ContentService.exceptions;

public class PostNotFoundByPublicIdException extends RuntimeException {
    public PostNotFoundByPublicIdException(String publicId) {
        super(String.format("Bài đăng có mã công khai %s không tìm thấy!", publicId));
    }
}
