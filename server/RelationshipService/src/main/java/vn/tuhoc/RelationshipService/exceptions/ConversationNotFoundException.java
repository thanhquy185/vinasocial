package vn.tuhoc.RelationshipService.exceptions;

public class ConversationNotFoundException extends RuntimeException {
    public ConversationNotFoundException() {
        super("Không tìm thấy cuộc trò chuyện.");
    }
}
