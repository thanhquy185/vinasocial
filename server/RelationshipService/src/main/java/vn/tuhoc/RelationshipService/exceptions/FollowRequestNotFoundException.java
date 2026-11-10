package vn.tuhoc.RelationshipService.exceptions;

public class FollowRequestNotFoundException extends RuntimeException {
    public FollowRequestNotFoundException() {
        super("Không tìm thấy yêu cầu theo dõi.");
    }
}
