package vn.tuhoc.RelationshipService.exceptions;

public class GroupMemberLimitReachedException extends RuntimeException {
    public GroupMemberLimitReachedException() {
        super("Nhóm chat tối đa 50 thành viên.");
    }
}
