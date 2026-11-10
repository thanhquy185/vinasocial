package vn.tuhoc.RelationshipService.exceptions;

public class ConversationRequestNotFoundException extends RuntimeException {
    public ConversationRequestNotFoundException() {
        super("Không tìm thấy yêu cầu nhắn tin.");
    }
}
