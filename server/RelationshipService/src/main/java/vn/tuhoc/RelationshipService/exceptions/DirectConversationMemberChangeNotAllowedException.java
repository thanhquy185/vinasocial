package vn.tuhoc.RelationshipService.exceptions;

public class DirectConversationMemberChangeNotAllowedException extends RuntimeException {
    public DirectConversationMemberChangeNotAllowedException(String message) {
        super(message);
    }
}
