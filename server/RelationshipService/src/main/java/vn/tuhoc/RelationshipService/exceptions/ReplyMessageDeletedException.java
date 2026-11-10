package vn.tuhoc.RelationshipService.exceptions;

public class ReplyMessageDeletedException extends RuntimeException {
    public ReplyMessageDeletedException() {
        super("Không thể trả lời tin nhắn đã bị xóa.");
    }
}
