package vn.tuhoc.RelationshipService.exceptions;

public class MessageEncryptionModeMismatchException extends RuntimeException {
    public MessageEncryptionModeMismatchException() {
        super("Nội dung tin nhắn không khớp với chế độ mã hóa hiện tại. Hãy tải lại trang và thử lại.");
    }
}
