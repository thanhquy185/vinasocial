package vn.tuhoc.RelationshipService.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.RelationshipService.dtos.events.NotificationCreatedEventDTO;
import vn.tuhoc.RelationshipService.services.interfaces.ProducerService;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProducerServiceImplement implements ProducerService {
    KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(
            Integer recipientId,
            Integer actorId,
            String type,
            String targetType,
            Integer targetId,
            Integer referenceId,
            String title,
            String message,
            Map<String, Object> data) {
        this.publishWithEventId(
                UUID.randomUUID().toString(), recipientId, actorId, type, targetType, targetId, referenceId,
                title, message, data);
    }

    @Override
    public void publishWithEventId(
            String eventId,
            Integer recipientId,
            Integer actorId,
            String type,
            String targetType,
            Integer targetId,
            Integer referenceId,
            String title,
            String message,
            Map<String, Object> data) {
        NotificationCreatedEventDTO event = NotificationCreatedEventDTO.builder()
                .eventId(eventId)
                .recipientId(recipientId)
                .actorId(actorId)
                .type(type)
                .targetType(targetType)
                .targetId(targetId)
                .referenceId(referenceId)
                .title(title)
                .message(message)
                .data(data)
                .build();
        this.kafkaTemplate.send("notification_created", String.valueOf(recipientId), event);
    }
}
