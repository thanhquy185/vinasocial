package vn.tuhoc.MediaService.exceptions;

public class MediaEncryptionPolicyViolationException extends RuntimeException {
    public MediaEncryptionPolicyViolationException(String message) {
        super(message);
    }
}
