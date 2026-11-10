package vn.tuhoc.RelationshipService.exceptions;

public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException() {
        super("Không tìm thấy tin nhắn.");
    }
}
