package vn.tuhoc.RelationshipService.services.interfaces;

import java.util.Map;

public interface ProducerService {
    void publish(
            Integer recipientId,
            Integer actorId,
            String type,
            String targetType,
            Integer targetId,
            Integer referenceId,
            String title,
            String message,
            Map<String, Object> data);

    void publishWithEventId(
            String eventId,
            Integer recipientId,
            Integer actorId,
            String type,
            String targetType,
            Integer targetId,
            Integer referenceId,
            String title,
            String message,
            Map<String, Object> data);
}
