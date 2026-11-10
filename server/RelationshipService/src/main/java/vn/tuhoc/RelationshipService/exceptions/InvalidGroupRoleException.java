package vn.tuhoc.RelationshipService.exceptions;

public class InvalidGroupRoleException extends RuntimeException {
    public InvalidGroupRoleException() {
        super("Có thể gán vai trò ADMIN hoặc MEMBER; quyền OWNER được chuyển khi chủ nhóm rời nhóm.");
    }
}
