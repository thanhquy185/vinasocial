package vn.tuhoc.MediaService.exceptions;

public class FileNameIsEmptyException extends RuntimeException {
    public FileNameIsEmptyException() {
        super("Tên file không được để trống!");
    }
}
