package vn.tuhoc.NotificationService.dtos.responses;

import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.core.dtos.clients.UserInfoResClientDTO;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponseDTO {
    String id;
    Integer actorId;
    UserInfoResClientDTO actor;
    Integer recipientId;
    Integer targetId;
    String targetType;
    Integer referenceId;
    String createdAt;
    String readAt;
    String dismissedAt;
    String type;
    String title;
    String message;
    Map<String, Object> data;
    String status;
}
