package vn.tuhoc.MediaService.exceptions;

public class MediaNotFoundByIdException extends RuntimeException {
    public MediaNotFoundByIdException(String id) {
        super(String.format("Phương tiện có mã %s không tìm thấy!", id));
    }
}
