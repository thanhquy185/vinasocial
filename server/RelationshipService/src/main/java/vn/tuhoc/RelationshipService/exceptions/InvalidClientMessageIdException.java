package vn.tuhoc.RelationshipService.exceptions;

public class InvalidClientMessageIdException extends RuntimeException {
    public InvalidClientMessageIdException() {
        super("clientMessageId phải là UUID hợp lệ.");
    }
}
