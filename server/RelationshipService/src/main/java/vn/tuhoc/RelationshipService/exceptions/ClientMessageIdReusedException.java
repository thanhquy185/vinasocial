package vn.tuhoc.RelationshipService.exceptions;

public class ClientMessageIdReusedException extends RuntimeException {
    public ClientMessageIdReusedException(String message) {
        super(message);
    }
}
