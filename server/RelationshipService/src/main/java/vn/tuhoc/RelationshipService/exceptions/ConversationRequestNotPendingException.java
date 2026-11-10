package vn.tuhoc.RelationshipService.exceptions;

public class ConversationRequestNotPendingException extends RuntimeException {
    public ConversationRequestNotPendingException() {
        super("Yêu cầu nhắn tin này không còn chờ duyệt.");
    }
}
