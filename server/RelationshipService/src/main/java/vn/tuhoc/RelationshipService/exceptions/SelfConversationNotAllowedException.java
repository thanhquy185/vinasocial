package vn.tuhoc.RelationshipService.exceptions;

public class SelfConversationNotAllowedException extends RuntimeException {
    public SelfConversationNotAllowedException() {
        super("Không thể tạo cuộc trò chuyện với chính tài khoản của bạn.");
    }
}
