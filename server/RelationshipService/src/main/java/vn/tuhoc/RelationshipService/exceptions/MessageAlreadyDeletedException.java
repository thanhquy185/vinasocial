package vn.tuhoc.RelationshipService.exceptions;

public class MessageAlreadyDeletedException extends RuntimeException {
    public MessageAlreadyDeletedException() {
        super("Tin nhắn đã bị xóa.");
    }
}
