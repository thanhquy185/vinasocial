package vn.tuhoc.UserService.customs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vn.tuhoc.UserService.exceptions.AuthAccessTokenIsNotValidException;
import vn.tuhoc.UserService.exceptions.AuthOtpAlreadyVerifiedException;
import vn.tuhoc.UserService.exceptions.AuthMfaSettingException;
import vn.tuhoc.UserService.exceptions.AuthOtpExpiredException;
import vn.tuhoc.UserService.exceptions.AuthOtpInvalidCodeException;
import vn.tuhoc.UserService.exceptions.AuthOtpIsNotVerifiedException;
import vn.tuhoc.UserService.exceptions.AuthOtpNotFoundByPublicIdException;
import vn.tuhoc.UserService.exceptions.AuthSessionNotFoundByRefreshTokenException;
import vn.tuhoc.UserService.exceptions.AuthSessionNotFoundByDeviceIdException;
import vn.tuhoc.UserService.exceptions.ProfileLinkNotFoundByProfileIdException;
import vn.tuhoc.UserService.exceptions.ProfileNotFoundByEmailException;
import vn.tuhoc.UserService.exceptions.ProfileNotFoundByIdException;
import vn.tuhoc.UserService.exceptions.ProfileNotFoundByPhoneException;
import vn.tuhoc.UserService.exceptions.ProfileNotFoundByUserIdException;
import vn.tuhoc.UserService.exceptions.ProfileNotFoundByUsernameException;
import vn.tuhoc.UserService.exceptions.ProfileNotificationSettingNotFoundByProfileIdException;
import vn.tuhoc.UserService.exceptions.ProfilePrivacySettingNotFoundByProfileIdException;
import vn.tuhoc.UserService.exceptions.ProfileStatsNotFoundByProfileIdException;
import vn.tuhoc.UserService.exceptions.ProfileUsernameIsExistsException;
import vn.tuhoc.UserService.exceptions.ProfilePropertyAlreadyExistsException;
import vn.tuhoc.UserService.exceptions.ProfileAvatarInvalidException;
import vn.tuhoc.UserService.exceptions.ProfileSettingUpdateInvalidException;
import vn.tuhoc.UserService.exceptions.UserNotFoundByIdException;
import vn.tuhoc.UserService.exceptions.UserNotFoundByPublicIdException;
import vn.tuhoc.UserService.exceptions.UserNotFoundByUsernameAndPasswordException;
import vn.tuhoc.UserService.exceptions.UserNotFoundByUsernameException;
import vn.tuhoc.UserService.exceptions.UsernameNotFoundByTypeAndValueException;
import vn.tuhoc.UserService.exceptions.UserPasswordIsNotMatchException;
import vn.tuhoc.UserService.exceptions.UserPasswordIsUsingException;
import vn.tuhoc.UserService.exceptions.UsernameNotFoundByIdException;
import vn.tuhoc.UserService.exceptions.UsernameValueIsExistsException;
import vn.tuhoc.core.utils.ExceptionHandlerUtil;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestControllerAdvice
public class LocalExceptionHandlerCustom {
        @ExceptionHandler({
                        UserNotFoundByIdException.class,
                        UserNotFoundByPublicIdException.class,
                        UserNotFoundByUsernameException.class,
                        UserNotFoundByUsernameAndPasswordException.class,
                        UsernameNotFoundByTypeAndValueException.class,
                        UsernameNotFoundByIdException.class,
                        AuthSessionNotFoundByRefreshTokenException.class,
                        AuthSessionNotFoundByDeviceIdException.class,
                        AuthOtpNotFoundByPublicIdException.class,
                        ProfileNotFoundByIdException.class,
                        ProfileNotFoundByUserIdException.class,
                        ProfileNotFoundByPhoneException.class,
                        ProfileNotFoundByEmailException.class,
                        ProfileNotFoundByUsernameException.class,
                        ProfileStatsNotFoundByProfileIdException.class,
                        ProfileNotificationSettingNotFoundByProfileIdException.class,
                        ProfilePrivacySettingNotFoundByProfileIdException.class,
                        ProfileLinkNotFoundByProfileIdException.class
        })
        public ResponseEntity<?> handleEntityNotFoundByIdException(RuntimeException exception) {
                return RestResponseUtil.notFound(
                                ExceptionHandlerUtil.formatError(exception),
                                exception.getMessage());
        }

        @ExceptionHandler({
                        UserPasswordIsUsingException.class,
                        UsernameValueIsExistsException.class,
                        UserPasswordIsNotMatchException.class,
                        UserPasswordIsUsingException.class,
                        AuthAccessTokenIsNotValidException.class,
                        AuthOtpExpiredException.class,
                        AuthOtpAlreadyVerifiedException.class,
                        AuthOtpIsNotVerifiedException.class,
                        AuthOtpInvalidCodeException.class,
                        AuthMfaSettingException.class,
                        ProfileUsernameIsExistsException.class,
                        ProfilePropertyAlreadyExistsException.class,
                        ProfileAvatarInvalidException.class,
                        ProfileSettingUpdateInvalidException.class
        })

        public ResponseEntity<?> handlePropertyEntityIsError(RuntimeException exception) {
                return RestResponseUtil.badRequest(
                                ExceptionHandlerUtil.formatError(exception),
                                exception.getMessage());
        }
}
