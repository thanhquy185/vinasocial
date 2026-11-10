package vn.tuhoc.ContentService.customs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vn.tuhoc.ContentService.exceptions.PostMediaInvalidException;
import vn.tuhoc.ContentService.exceptions.PostMediaNotFoundByIdException;
import vn.tuhoc.ContentService.exceptions.PostMediaNotFoundByMediaIdException;
import vn.tuhoc.ContentService.exceptions.PostMediaNotFoundByPostIdException;
import vn.tuhoc.ContentService.exceptions.PostNotFoundByIdException;
import vn.tuhoc.ContentService.exceptions.PostNotFoundByPublicIdException;
import vn.tuhoc.ContentService.exceptions.PostSettingNotFoundByPostIdException;
import vn.tuhoc.ContentService.exceptions.PostStatsNotFoundByPostIdException;
import vn.tuhoc.ContentService.exceptions.StoryNotFoundByIdException;
import vn.tuhoc.ContentService.exceptions.StoryHighlightNotFoundException;
import vn.tuhoc.ContentService.exceptions.PostTagInvalidException;
import vn.tuhoc.ContentService.exceptions.StoryHighlightInvalidException;
import vn.tuhoc.core.utils.ExceptionHandlerUtil;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestControllerAdvice
public class LocalExceptionHandlerCustom {
    @ExceptionHandler({
            PostNotFoundByIdException.class,
            PostNotFoundByPublicIdException.class,
            PostMediaNotFoundByIdException.class,
            PostMediaNotFoundByPostIdException.class,
            PostMediaNotFoundByMediaIdException.class,
            PostSettingNotFoundByPostIdException.class,
            PostStatsNotFoundByPostIdException.class,
            StoryNotFoundByIdException.class,
            StoryHighlightNotFoundException.class,
    })
    public ResponseEntity<?> handleEntityNotFoundByIdException(RuntimeException exception) {
        return RestResponseUtil.notFound(
                ExceptionHandlerUtil.formatError(exception),
                exception.getMessage());
    }

    @ExceptionHandler({
            PostMediaInvalidException.class,
            PostTagInvalidException.class,
            StoryHighlightInvalidException.class
    })
    public ResponseEntity<?> handlePropertyEntityIsError(RuntimeException exception) {
        return RestResponseUtil.badRequest(
                ExceptionHandlerUtil.formatError(exception),
                exception.getMessage());
    }
}
