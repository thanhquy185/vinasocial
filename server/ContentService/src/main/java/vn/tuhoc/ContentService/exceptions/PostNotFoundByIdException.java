package vn.tuhoc.ContentService.exceptions;

public class PostNotFoundByIdException extends RuntimeException {
    public PostNotFoundByIdException(Integer id) {
        super(String.format("Bài đăng có mã %s không tìm thấy!", id));
    }
}
