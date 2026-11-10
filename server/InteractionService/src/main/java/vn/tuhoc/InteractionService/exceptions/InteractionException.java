package vn.tuhoc.InteractionService.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class InteractionException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public InteractionException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}
