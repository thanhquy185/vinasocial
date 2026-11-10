package vn.tuhoc.ContentService.exceptions;

public class PostMediaNotFoundByMediaIdException extends RuntimeException {
    public PostMediaNotFoundByMediaIdException(String mediaId) {
        super(String.format("Phương tiện bài đăng có mã phương tiện %s không tìm thấy!", mediaId));
    }
}
