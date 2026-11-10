package vn.tuhoc.RelationshipService.exceptions;

public class FollowRequestNotPendingException extends RuntimeException {
    public FollowRequestNotPendingException() {
        super("Yêu cầu theo dõi này không còn chờ duyệt.");
    }
}
