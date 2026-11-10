package vn.tuhoc.InteractionService.customs;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vn.tuhoc.InteractionService.exceptions.InteractionException;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.ExceptionHandlerUtil;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestControllerAdvice
public class LocalExceptionHandlerCustom {
    @ExceptionHandler(InteractionException.class)
    public ResponseEntity<RestResponseDTO<Object>> handleInteractionException(InteractionException exception) {
        return RestResponseUtil.error(exception.getStatus(), exception.getErrorCode(), exception.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<?> handleDuplicateKeyException(DuplicateKeyException exception) {
        return RestResponseUtil.conflict(
                ExceptionHandlerUtil.formatError(exception),
                "Dữ liệu tương tác đã tồn tại.");
    }
}
