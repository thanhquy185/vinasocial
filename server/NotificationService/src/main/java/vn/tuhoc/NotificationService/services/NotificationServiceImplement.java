package vn.tuhoc.NotificationService.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.NotificationService.clients.UserClient;
import vn.tuhoc.NotificationService.domains.entities.NotificationEntity;
import vn.tuhoc.NotificationService.domains.entities.NotificationPreferenceEntity;
import vn.tuhoc.NotificationService.domains.enums.NotificationStatusEnum;
import vn.tuhoc.NotificationService.domains.enums.NotificationTargetTypeEnum;
import vn.tuhoc.NotificationService.domains.enums.NotificationTypeEnum;
import vn.tuhoc.NotificationService.dtos.events.NotificationCreatedEventDTO;
import vn.tuhoc.NotificationService.dtos.events.NotificationPreferencesUpdatedEventDTO;
import vn.tuhoc.NotificationService.dtos.responses.NotificationActionResponseDTO;
import vn.tuhoc.NotificationService.dtos.responses.NotificationResponseDTO;
import vn.tuhoc.NotificationService.dtos.requests.UserTestConnectRequestDTO;
import vn.tuhoc.NotificationService.exceptions.InvalidNotificationIdException;
import vn.tuhoc.NotificationService.exceptions.InvalidPageException;
import vn.tuhoc.NotificationService.exceptions.NotificationNotFoundException;
import vn.tuhoc.NotificationService.repositories.NotificationPreferenceRepository;
import vn.tuhoc.NotificationService.repositories.NotificationRepository;
import vn.tuhoc.NotificationService.services.interfaces.NotificationService;
import vn.tuhoc.core.dtos.clients.UserInfoReqClientDTO;
import vn.tuhoc.core.dtos.clients.UserInfoResClientDTO;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.utils.PageResponseUtil;
import vn.tuhoc.core.utils.TimeUtil;
import vn.tuhoc.security.utils.SecurityUtil;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImplement implements NotificationService {
    Set<String> SAFE_METADATA_KEYS = Set.of(
            "conversationId", "messageId", "messageType", "postId", "storyId", "followRequestId", "route",
            "status");

    NotificationRepository notificationRepository;
    NotificationPreferenceRepository preferenceRepository;
    MongoTemplate mongoTemplate;
    UserClient userClient;

    private boolean isEnabled(NotificationPreferenceEntity preference, NotificationTypeEnum type) {
        if (preference == null || type == NotificationTypeEnum.SYSTEM) {
            return true;
        }
        return switch (type) {
            case POST_CREATE_NEW, POST_LIKED, POST_COMMENTED, POST_REUPED, POST_UPDATED, POST_STATUS_CHANGED,
                    POST_FLAGGED, POST_REJECTED ->
                    this.defaultEnabled(preference.getPostEnabled());
            case STORY_CREATE_NEW, STORY_LIKED, STORY_REPLIED, STORY_FLAGGED, STORY_REJECTED ->
                    this.defaultEnabled(preference.getStoryEnabled());
            case POST_COLLABORATOR -> this.defaultEnabled(preference.getCollaboratorEnabled());
            case POST_TAG, POST_MENTION, COMMENT_MENTION -> this.defaultEnabled(preference.getTagEnabled());
            case SEND_MESSAGE -> this.defaultEnabled(preference.getMessageEnabled());
            case FOLLOW_REQUEST, FOLLOW_ACCEPTED, FOLLOWED -> this.defaultEnabled(preference.getFollowEnabled());
            case COMMENT_LIKED, COMMENT_REPLIED, COMMENT_FLAGGED, COMMENT_REJECTED ->
                    this.defaultEnabled(preference.getReplyCommentEnabled());
            default -> true;
        };
    }

    private boolean defaultEnabled(Boolean value) {
        return value == null || value;
    }

    private Map<String, Object> filterMetadata(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return Map.of();
        }
        return metadata.entrySet().stream()
                .filter(entry -> this.SAFE_METADATA_KEYS.contains(entry.getKey()))
                .filter(entry -> entry.getValue() instanceof String
                        || entry.getValue() instanceof Number
                        || entry.getValue() instanceof Boolean)
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private NotificationEntity getOwnedNotification(String notificationId) {
        if (notificationId == null || notificationId.isBlank()) {
            throw new InvalidNotificationIdException();
        }
        return this.notificationRepository.findByIdAndRecipientId(notificationId, this.getCurrentUserId())
                .orElseThrow(NotificationNotFoundException::new);
    }

    private Map<Integer, UserInfoResClientDTO> getActorMap(NotificationEntity notification) {
        return this.getUserInfoMap(notification.getActorId() == null ? List.of() : List.of(notification.getActorId()));
    }

    private Map<Integer, UserInfoResClientDTO> getUserInfoMap(List<Integer> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        List<UserInfoResClientDTO> userInfo = this.userClient.getInfo(
                UserInfoReqClientDTO.builder().userIds(userIds).build());
        if (userInfo == null) {
            return Map.of();
        }
        return userInfo.stream()
                .filter(item -> item.getId() != null)
                .collect(Collectors.toMap(UserInfoResClientDTO::getId, Function.identity(), (first, ignored) -> first));
    }

    private NotificationResponseDTO toResponse(
            NotificationEntity entity,
            Map<Integer, UserInfoResClientDTO> actors) {
        return NotificationResponseDTO.builder()
                .id(entity.getId())
                .actorId(entity.getActorId())
                .actor(entity.getActorId() == null ? null : actors.get(entity.getActorId()))
                .recipientId(entity.getRecipientId())
                .targetId(entity.getTargetId())
                .targetType(entity.getTargetType() == null ? null : entity.getTargetType().name())
                .referenceId(entity.getReferenceId())
                .createdAt(entity.getCreatedAt())
                .readAt(entity.getReadAt())
                .dismissedAt(entity.getDismissedAt())
                .type(entity.getType().name())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .data(entity.getData())
                .status(entity.getStatus().name())
                .build();
    }

    private Integer getCurrentUserId() {
        return SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("Bạn cần đăng nhập để xem thông báo."));
    }

    @Override
    public String notificationTestConnect(UserTestConnectRequestDTO userTestConnect) {
        return String.format(
                "Thông báo người dùng mới đăng ký có họ tên là %s và tên tài khoản là %s",
                userTestConnect.getFullname(),
                userTestConnect.getUsername());
    }

    @Override
    public PageResponseDTO<NotificationResponseDTO> getMine(int page, int size, boolean unreadOnly) {
        Integer recipientId = this.getCurrentUserId();
        if (page < 0 || size < 1 || size > 50) {
            throw new InvalidPageException();
        }

        Collection<NotificationStatusEnum> statuses = unreadOnly
                ? List.of(NotificationStatusEnum.ACTIVE)
                : List.of(NotificationStatusEnum.ACTIVE, NotificationStatusEnum.READ);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<NotificationEntity> notifications = this.notificationRepository
                .findAllByRecipientIdAndStatusIn(recipientId, statuses, pageable);

        List<Integer> actorIds = notifications.getContent().stream()
                .map(NotificationEntity::getActorId)
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();
        Map<Integer, UserInfoResClientDTO> actors = this.getUserInfoMap(actorIds);

        return PageResponseUtil.convert(notifications.map(item -> this.toResponse(item, actors)));
    }

    @Override
    public long getUnreadCount() {
        return this.notificationRepository.countByRecipientIdAndStatus(
                this.getCurrentUserId(), NotificationStatusEnum.ACTIVE);
    }

    @Override
    public NotificationResponseDTO markAsRead(String notificationId) {
        NotificationEntity notification = this.getOwnedNotification(notificationId);
        if (notification.getStatus() == NotificationStatusEnum.ACTIVE) {
            notification.setStatus(NotificationStatusEnum.READ);
            notification.setReadAt(TimeUtil.getCurrentDatetime());
            notification = this.notificationRepository.save(notification);
        }
        return this.toResponse(notification, this.getActorMap(notification));
    }

    @Override
    public NotificationActionResponseDTO markAllAsRead() {
        Integer recipientId = this.getCurrentUserId();
        String now = TimeUtil.getCurrentDatetime();
        var result = this.mongoTemplate.updateMulti(
                Query.query(Criteria.where("recipientId").is(recipientId)
                        .and("status").is(NotificationStatusEnum.ACTIVE)),
                new Update().set("status", NotificationStatusEnum.READ).set("readAt", now),
                NotificationEntity.class);
        return new NotificationActionResponseDTO(result.getModifiedCount());
    }

    @Override
    public NotificationResponseDTO dismiss(String notificationId) {
        NotificationEntity notification = this.getOwnedNotification(notificationId);
        if (notification.getStatus() != NotificationStatusEnum.DISMISSED) {
            notification.setStatus(NotificationStatusEnum.DISMISSED);
            notification.setDismissedAt(TimeUtil.getCurrentDatetime());
            notification = this.notificationRepository.save(notification);
        }
        return this.toResponse(notification, this.getActorMap(notification));
    }

    @Override
    public void handleCreatedEvent(NotificationCreatedEventDTO event) {
        if (event == null || event.getRecipientId() == null || event.getRecipientId() < 1
                || event.getEventId() == null || event.getEventId().isBlank()) {
            throw new IllegalArgumentException("Notification event requires eventId and a valid recipientId.");
        }
        String eventKey = event.getEventId().trim() + ":" + event.getRecipientId();
        if (this.notificationRepository.findByEventKey(eventKey).isPresent()) {
            return;
        }

        NotificationTypeEnum type = NotificationTypeEnum.valueOf(event.getType());
        NotificationTargetTypeEnum targetType = event.getTargetType() == null || event.getTargetType().isBlank()
                ? null
                : NotificationTargetTypeEnum.valueOf(event.getTargetType());
        if (event.getTitle() == null || event.getTitle().isBlank() || event.getTitle().length() > 100
                || event.getMessage() == null || event.getMessage().isBlank() || event.getMessage().length() > 255) {
            throw new IllegalArgumentException("Notification title or message is empty or too long.");
        }

        NotificationPreferenceEntity preference = this.preferenceRepository.findById(event.getRecipientId()).orElse(null);
        boolean enabled = this.isEnabled(preference, type);
        String now = TimeUtil.getCurrentDatetime();
        NotificationEntity notification = NotificationEntity.builder()
                .eventKey(eventKey)
                .recipientId(event.getRecipientId())
                .actorId(event.getActorId())
                .targetId(event.getTargetId())
                .targetType(targetType)
                .referenceId(event.getReferenceId())
                .createdAt(now)
                .type(type)
                .title(event.getTitle().trim())
                .message(event.getMessage().trim())
                .data(this.filterMetadata(event.getData()))
                .status(enabled ? NotificationStatusEnum.ACTIVE : NotificationStatusEnum.DISMISSED)
                .dismissedAt(enabled ? null : now)
                .build();
        this.notificationRepository.save(notification);
    }

    @Override
    public void handlePreferencesUpdatedEvent(NotificationPreferencesUpdatedEventDTO event) {
        if (event == null || event.getUserId() == null || event.getUserId() < 1) {
            throw new IllegalArgumentException("Notification preferences event requires a valid userId.");
        }
        this.preferenceRepository.save(NotificationPreferenceEntity.builder()
                .userId(event.getUserId())
                .postEnabled(event.getPostEnabled())
                .storyEnabled(event.getStoryEnabled())
                .collaboratorEnabled(event.getCollaboratorEnabled())
                .tagEnabled(event.getTagEnabled())
                .messageEnabled(event.getMessageEnabled())
                .followEnabled(event.getFollowEnabled())
                .replyCommentEnabled(event.getReplyCommentEnabled())
                .build());
    }

}
