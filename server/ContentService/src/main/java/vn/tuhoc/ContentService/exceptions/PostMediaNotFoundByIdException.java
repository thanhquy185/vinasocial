package vn.tuhoc.ContentService.exceptions;

public class PostMediaNotFoundByIdException extends RuntimeException {
    public PostMediaNotFoundByIdException(Integer id) {
        super(String.format("Phương tiện bài đăng có mã %s không tìm thấy!", id));
    }
}
