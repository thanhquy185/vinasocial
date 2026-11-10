package vn.tuhoc.ContentService.exceptions;

public class StoryNotFoundByIdException extends RuntimeException {
    public StoryNotFoundByIdException(Integer id) {
        super(String.format("Câu chuyện có mã %s không tìm thấy!", id));
    }
}
