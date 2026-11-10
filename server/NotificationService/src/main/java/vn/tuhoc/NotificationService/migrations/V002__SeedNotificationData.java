package vn.tuhoc.NotificationService.migrations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.NotificationService.domains.entities.NotificationEntity;
import vn.tuhoc.NotificationService.domains.enums.NotificationStatusEnum;
import vn.tuhoc.NotificationService.domains.enums.NotificationTargetTypeEnum;
import vn.tuhoc.NotificationService.domains.enums.NotificationTypeEnum;

@ChangeUnit(id = "002-seed-notification-data", order = "002", author = "quy")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class V002__SeedNotificationData {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final List<String> DEMO_NOTIFICATION_TYPES =
            List.of("follow-request", "post-like", "comment", "message", "system");

    final MongoTemplate mongoTemplate;

    @Execution
    public void execution() {
        for (int recipientId = 2; recipientId <= 21; recipientId++) {
            int nextUserId = recipientId == 21 ? 2 : recipientId + 1;
            int previousUserId = recipientId == 2 ? 21 : recipientId - 1;
            this.seed(this.followRequest(recipientId, nextUserId));
            this.seed(this.postLike(recipientId, previousUserId));
            this.seed(this.comment(recipientId, nextUserId));
            this.seed(this.message(recipientId, previousUserId));
            this.seed(this.systemNotice(recipientId));
        }
    }

    @RollbackExecution
    public void rollback() {
        List<String> ids = new ArrayList<>();
        for (int recipientId = 2; recipientId <= 21; recipientId++) {
            for (String type : DEMO_NOTIFICATION_TYPES) {
                ids.add("demo-" + recipientId + "-" + type);
            }
        }
        this.mongoTemplate.remove(Query.query(Criteria.where("_id").in(ids)), NotificationEntity.class);
    }

    private void seed(NotificationEntity notification) {
        if (!this.mongoTemplate.exists(
                Query.query(Criteria.where("_id").is(notification.getId())), NotificationEntity.class)) {
            this.mongoTemplate.save(notification);
        }
    }

    private NotificationEntity followRequest(int recipientId, int actorId) {
        return this.notification(
                recipientId, "follow-request", actorId, NotificationTypeEnum.FOLLOW_REQUEST,
                NotificationTargetTypeEnum.FOLLOW_REQUEST, recipientId, recipientId * 100 + 1,
                "Yêu cầu theo dõi mới", "đã gửi cho bạn yêu cầu theo dõi.",
                NotificationStatusEnum.ACTIVE, null, -5, Map.of("followRequestId", recipientId * 100 + 1));
    }

    private NotificationEntity postLike(int recipientId, int actorId) {
        return this.notification(
                recipientId, "post-like", actorId, NotificationTypeEnum.POST_LIKED,
                NotificationTargetTypeEnum.POST, recipientId * 100 + 1, recipientId * 100 + 10,
                "Lượt thích mới", "đã thích bài viết của bạn.",
                NotificationStatusEnum.READ, -15, -20, Map.of("postId", recipientId * 100 + 1));
    }

    private NotificationEntity comment(int recipientId, int actorId) {
        return this.notification(
                recipientId, "comment", actorId, NotificationTypeEnum.POST_COMMENTED,
                NotificationTargetTypeEnum.COMMENT, recipientId * 100 + 2, recipientId * 100 + 11,
                "Bình luận mới", "đã bình luận về bài viết của bạn.",
                NotificationStatusEnum.ACTIVE, null, -60, Map.of("postId", recipientId * 100 + 2));
    }

    private NotificationEntity message(int recipientId, int actorId) {
        return this.notification(
                recipientId, "message", actorId, NotificationTypeEnum.SEND_MESSAGE,
                NotificationTargetTypeEnum.CONVERSATION, recipientId * 10 + 1, recipientId * 100 + 12,
                "Tin nhắn mới", "đã gửi cho bạn một tin nhắn.",
                NotificationStatusEnum.ACTIVE, null, -1_440,
                Map.of("conversationId", recipientId * 10 + 1, "messageId", recipientId * 100 + 12,
                        "messageType", "TEXT"));
    }

    private NotificationEntity systemNotice(int recipientId) {
        return this.notification(
                recipientId, "system", null, NotificationTypeEnum.SYSTEM,
                null, null, null,
                "Chào mừng đến với VinaSocial", "Hãy hoàn thiện hồ sơ để bạn bè dễ dàng nhận ra bạn.",
                NotificationStatusEnum.READ, -2_860, -2_880, Map.of("route", "/profile/settings"));
    }

    private NotificationEntity notification(
            int recipientId,
            String suffix,
            Integer actorId,
            NotificationTypeEnum type,
            NotificationTargetTypeEnum targetType,
            Integer targetId,
            Integer referenceId,
            String title,
            String message,
            NotificationStatusEnum status,
            Integer readMinutesAgo,
            int createdMinutesAgo,
            Map<String, Object> data) {
        return NotificationEntity.builder()
                .id("demo-" + recipientId + "-" + suffix)
                .eventKey("demo:" + recipientId + ":" + suffix)
                .recipientId(recipientId)
                .actorId(actorId)
                .targetId(targetId)
                .targetType(targetType)
                .referenceId(referenceId)
                .createdAt(this.time(createdMinutesAgo))
                .readAt(readMinutesAgo == null ? null : this.time(readMinutesAgo))
                .type(type)
                .title(title)
                .message(message)
                .data(data)
                .status(status)
                .build();
    }

    private String time(int minutesAgo) {
        return LocalDateTime.now().plusMinutes(minutesAgo).format(DATE_FORMAT);
    }
}
