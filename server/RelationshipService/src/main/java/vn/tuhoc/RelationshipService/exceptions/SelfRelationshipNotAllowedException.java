package vn.tuhoc.RelationshipService.exceptions;

public class SelfRelationshipNotAllowedException extends RuntimeException {
    public SelfRelationshipNotAllowedException() {
        super("Không thể tạo mối quan hệ với chính tài khoản của bạn.");
    }
}
