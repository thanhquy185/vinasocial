package vn.tuhoc.RelationshipService.exceptions;

public class DuplicateGroupMemberException extends RuntimeException {
    public DuplicateGroupMemberException() {
        super("Danh sách thành viên nhóm bị trùng.");
    }
}
