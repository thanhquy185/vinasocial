package vn.tuhoc.RelationshipService.customs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vn.tuhoc.RelationshipService.exceptions.ClientMessageIdReusedException;
import vn.tuhoc.RelationshipService.exceptions.ConversationNotFoundException;
import vn.tuhoc.RelationshipService.exceptions.ConversationRequestNotFoundException;
import vn.tuhoc.RelationshipService.exceptions.ConversationRequestNotPendingException;
import vn.tuhoc.RelationshipService.exceptions.DirectConversationMemberChangeNotAllowedException;
import vn.tuhoc.RelationshipService.exceptions.DuplicateGroupMemberException;
import vn.tuhoc.RelationshipService.exceptions.E2eeDeviceKeyConflictException;
import vn.tuhoc.RelationshipService.exceptions.E2eePairingCodeInvalidException;
import vn.tuhoc.RelationshipService.exceptions.FollowRequestNotFoundException;
import vn.tuhoc.RelationshipService.exceptions.FollowRequestNotPendingException;
import vn.tuhoc.RelationshipService.exceptions.GroupMemberLimitReachedException;
import vn.tuhoc.RelationshipService.exceptions.InvalidClientMessageIdException;
import vn.tuhoc.RelationshipService.exceptions.InvalidGroupRoleException;
import vn.tuhoc.RelationshipService.exceptions.InvalidPageException;
import vn.tuhoc.RelationshipService.exceptions.MessageAlreadyDeletedException;
import vn.tuhoc.RelationshipService.exceptions.MessageEncryptionModeMismatchException;
import vn.tuhoc.RelationshipService.exceptions.MessageNotFoundException;
import vn.tuhoc.RelationshipService.exceptions.MessageRequestPendingException;
import vn.tuhoc.RelationshipService.exceptions.OwnerRoleChangeNotAllowedException;
import vn.tuhoc.RelationshipService.exceptions.ReplyMessageDeletedException;
import vn.tuhoc.RelationshipService.exceptions.ReplyMessageNotFoundException;
import vn.tuhoc.RelationshipService.exceptions.SelfConversationNotAllowedException;
import vn.tuhoc.RelationshipService.exceptions.SelfRelationshipNotAllowedException;
import vn.tuhoc.RelationshipService.exceptions.UserNotFoundException;
import vn.tuhoc.RelationshipService.exceptions.UserServiceUnavailableException;
import vn.tuhoc.core.utils.ExceptionHandlerUtil;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestControllerAdvice
public class LocalExceptionHandlerCustom {
    @ExceptionHandler({
            DuplicateGroupMemberException.class,
            E2eePairingCodeInvalidException.class,
            InvalidClientMessageIdException.class,
            InvalidGroupRoleException.class,
            InvalidPageException.class,
            MessageEncryptionModeMismatchException.class,
            OwnerRoleChangeNotAllowedException.class,
            SelfConversationNotAllowedException.class,
            SelfRelationshipNotAllowedException.class
    })
    public ResponseEntity<?> handleBadRequest(RuntimeException exception) {
        return RestResponseUtil.badRequest(
                ExceptionHandlerUtil.formatError(exception),
                exception.getMessage());
    }

    @ExceptionHandler({
            ConversationNotFoundException.class,
            ConversationRequestNotFoundException.class,
            FollowRequestNotFoundException.class,
            MessageNotFoundException.class,
            ReplyMessageNotFoundException.class,
            UserNotFoundException.class
    })
    public ResponseEntity<?> handleNotFound(RuntimeException exception) {
        return RestResponseUtil.notFound(
                ExceptionHandlerUtil.formatError(exception),
                exception.getMessage());
    }

    @ExceptionHandler({
            ClientMessageIdReusedException.class,
            ConversationRequestNotPendingException.class,
            DirectConversationMemberChangeNotAllowedException.class,
            E2eeDeviceKeyConflictException.class,
            FollowRequestNotPendingException.class,
            GroupMemberLimitReachedException.class,
            MessageAlreadyDeletedException.class,
            MessageRequestPendingException.class,
            ReplyMessageDeletedException.class
    })
    public ResponseEntity<?> handleConflict(RuntimeException exception) {
        return RestResponseUtil.conflict(
                ExceptionHandlerUtil.formatError(exception),
                exception.getMessage());
    }

    @ExceptionHandler(UserServiceUnavailableException.class)
    public ResponseEntity<?> handleUserServiceUnavailable(
            UserServiceUnavailableException exception) {
        return RestResponseUtil.serviceUnavailable(
                ExceptionHandlerUtil.formatError(exception),
                exception.getMessage());
    }

}
