package vn.tuhoc.ContentService.services.interfaces;

import java.util.Map;

public interface ProducerService {
    void publish(
            Integer recipientId,
            String type,
            String targetType,
            Integer targetId,
            String title,
            String message,
            Map<String, Object> data);

    void publishPostCreated(Integer authorId, Integer postId, String privacy);

    void publishPostUpdated(Integer authorId, Integer postId, String privacy);
}
