package vn.tuhoc.NotificationService.domains.entities;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.NotificationService.domains.enums.NotificationStatusEnum;
import vn.tuhoc.NotificationService.domains.enums.NotificationTargetTypeEnum;
import vn.tuhoc.NotificationService.domains.enums.NotificationTypeEnum;

@Document(collection = "notifications")
@CompoundIndexes({
        @CompoundIndex(name = "idx_notifications_inbox", def = "{'recipientId': 1, 'status': 1, 'createdAt': -1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationEntity {
    @Id
    String id;

    @Indexed(unique = true, sparse = true)
    String eventKey;

    @Indexed
    Integer recipientId;

    Integer actorId;

    Integer targetId;

    NotificationTargetTypeEnum targetType;

    Integer referenceId;

    String createdAt;

    String readAt;

    String dismissedAt;

    NotificationTypeEnum type;

    String title;

    String message;

    Map<String, Object> data;

    NotificationStatusEnum status;
}
