package vn.tuhoc.NotificationService.customs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vn.tuhoc.NotificationService.exceptions.EmailOTPCantNotSendException;
import vn.tuhoc.NotificationService.exceptions.InvalidNotificationIdException;
import vn.tuhoc.NotificationService.exceptions.InvalidPageException;
import vn.tuhoc.NotificationService.exceptions.NotificationNotFoundException;
import vn.tuhoc.NotificationService.exceptions.SmsOTPCantNotSendException;
import vn.tuhoc.core.utils.ExceptionHandlerUtil;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestControllerAdvice
public class LocalExceptionHandlerCustom {
    @ExceptionHandler({
            EmailOTPCantNotSendException.class,
            SmsOTPCantNotSendException.class,
            InvalidNotificationIdException.class,
            InvalidPageException.class
    })
    public ResponseEntity<?> handleBadRequest(RuntimeException exception) {
        return RestResponseUtil.badRequest(
                ExceptionHandlerUtil.formatError(exception),
                exception.getMessage());
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<?> handleNotificationNotFound(NotificationNotFoundException exception) {
        return RestResponseUtil.notFound(
                ExceptionHandlerUtil.formatError(exception),
                exception.getMessage());
    }

}
