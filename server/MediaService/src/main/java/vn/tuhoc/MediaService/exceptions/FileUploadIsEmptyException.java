package vn.tuhoc.MediaService.exceptions;

public class FileUploadIsEmptyException extends RuntimeException {
    public FileUploadIsEmptyException() {
        super("File tải lên không được để trống!");
    }
}
