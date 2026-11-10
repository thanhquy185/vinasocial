package vn.tuhoc.RelationshipService.exceptions;

public class MessageRequestPendingException extends RuntimeException {
    public MessageRequestPendingException(String message) {
        super(message);
    }
}
