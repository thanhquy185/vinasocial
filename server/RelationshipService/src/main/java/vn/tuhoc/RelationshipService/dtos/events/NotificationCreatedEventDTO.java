package vn.tuhoc.RelationshipService.dtos.events;

import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationCreatedEventDTO {
    String eventId;
    Integer recipientId;
    Integer actorId;
    Integer targetId;
    String targetType;
    Integer referenceId;
    String type;
    String title;
    String message;
    Map<String, Object> data;
}
