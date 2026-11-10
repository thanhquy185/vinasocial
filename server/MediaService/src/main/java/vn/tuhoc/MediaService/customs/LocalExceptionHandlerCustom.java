package vn.tuhoc.MediaService.customs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vn.tuhoc.MediaService.exceptions.FileNameIsEmptyException;
import vn.tuhoc.MediaService.exceptions.FileUploadIsEmptyException;
import vn.tuhoc.MediaService.exceptions.MediaEncryptionPolicyViolationException;
import vn.tuhoc.MediaService.exceptions.MediaNotFoundByIdException;
import vn.tuhoc.core.utils.ExceptionHandlerUtil;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestControllerAdvice
public class LocalExceptionHandlerCustom {
    @ExceptionHandler({
            MediaNotFoundByIdException.class
    })
    public ResponseEntity<?> handleEntityNotFoundByIdException(RuntimeException exception) {
        return RestResponseUtil.notFound(
                ExceptionHandlerUtil.formatError(exception),
                exception.getMessage());
    }

    @ExceptionHandler({
            FileNameIsEmptyException.class,
            FileUploadIsEmptyException.class,
            MediaEncryptionPolicyViolationException.class
    })
    public ResponseEntity<?> handlePropertyEntityIsError(RuntimeException exception) {
        return RestResponseUtil.badRequest(
                ExceptionHandlerUtil.formatError(exception),
                exception.getMessage());
    }
}
