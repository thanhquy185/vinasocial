package vn.tuhoc.RelationshipService.exceptions;

public class OwnerRoleChangeNotAllowedException extends RuntimeException {
    public OwnerRoleChangeNotAllowedException() {
        super("Chủ nhóm không thể tự thay đổi vai trò của mình.");
    }
}
