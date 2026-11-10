package vn.tuhoc.ContentService.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.dtos.events.NotificationCreatedEventDTO;
import vn.tuhoc.ContentService.dtos.events.PostPublishedEventDTO;
import vn.tuhoc.ContentService.dtos.events.PostUpdatedEventDTO;
import vn.tuhoc.ContentService.services.interfaces.ProducerService;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProducerServiceImplement implements ProducerService {
    KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(
            Integer recipientId,
            String type,
            String targetType,
            Integer targetId,
            String title,
            String message,
            Map<String, Object> data) {
        NotificationCreatedEventDTO event = NotificationCreatedEventDTO.builder()
                .eventId(UUID.randomUUID().toString())
                .recipientId(recipientId)
                .type(type)
                .targetType(targetType)
                .targetId(targetId)
                .title(title)
                .message(message)
                .data(data)
                .build();
        this.kafkaTemplate.send("notification_created", String.valueOf(recipientId), event);
    }

    @Override
    public void publishPostCreated(Integer authorId, Integer postId, String privacy) {
        PostPublishedEventDTO event = PostPublishedEventDTO.builder()
                .eventId(UUID.randomUUID().toString())
                .authorId(authorId)
                .postId(postId)
                .privacy(privacy)
                .build();
        this.kafkaTemplate.send("post_published", String.valueOf(authorId), event);
    }

    @Override
    public void publishPostUpdated(Integer authorId, Integer postId, String privacy) {
        PostUpdatedEventDTO event = PostUpdatedEventDTO.builder()
                .eventId(UUID.randomUUID().toString())
                .authorId(authorId)
                .postId(postId)
                .privacy(privacy)
                .build();
        this.kafkaTemplate.send("post_updated", String.valueOf(authorId), event);
    }
}
