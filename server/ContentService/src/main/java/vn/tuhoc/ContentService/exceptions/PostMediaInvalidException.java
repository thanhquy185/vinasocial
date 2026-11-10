package vn.tuhoc.ContentService.exceptions;

public class PostMediaInvalidException extends RuntimeException {
    public PostMediaInvalidException() {
        super("File phương tiện không hợp lệ hoặc không phù hợp với loại bài đăng!");
    }
}
