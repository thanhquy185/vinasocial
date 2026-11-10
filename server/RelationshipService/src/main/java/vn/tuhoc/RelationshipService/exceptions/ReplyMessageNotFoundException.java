package vn.tuhoc.RelationshipService.exceptions;

public class ReplyMessageNotFoundException extends RuntimeException {
    public ReplyMessageNotFoundException() {
        super("Không tìm thấy tin nhắn trả lời trong cuộc trò chuyện này.");
    }
}
