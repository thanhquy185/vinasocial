package vn.tuhoc.ContentService.exceptions;

public class StoryHighlightNotFoundException extends RuntimeException {
    public StoryHighlightNotFoundException(Integer id) {
        super("Không tìm thấy mục tin nổi bật với mã " + id + ".");
    }
}
