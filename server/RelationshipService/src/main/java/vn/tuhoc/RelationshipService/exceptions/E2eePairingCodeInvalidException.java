package vn.tuhoc.RelationshipService.exceptions;

public class E2eePairingCodeInvalidException extends RuntimeException {
    public E2eePairingCodeInvalidException() {
        super("Mã ghép nối không chính xác, đã hết hạn hoặc yêu cầu cần được duyệt lại.");
    }
}
