package vn.tuhoc.RelationshipService.exceptions;

public class InvalidPageException extends RuntimeException {
    public InvalidPageException() {
        super("page phải >= 0, size từ 1 đến 100.");
    }
}
