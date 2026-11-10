package vn.tuhoc.RelationshipService.services;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.RelationshipService.domains.entities.CloseFriendEntity;
import vn.tuhoc.RelationshipService.domains.entities.FollowEntity;
import vn.tuhoc.RelationshipService.dtos.events.PostPublishedEventDTO;
import vn.tuhoc.RelationshipService.dtos.events.PostUpdatedEventDTO;
import vn.tuhoc.RelationshipService.repositories.CloseFriendRepository;
import vn.tuhoc.RelationshipService.repositories.FollowRepository;
import vn.tuhoc.RelationshipService.services.interfaces.ConsumerService;
import vn.tuhoc.RelationshipService.services.interfaces.ProducerService;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConsumerServiceImplement implements ConsumerService {
    FollowRepository followRepository;
    CloseFriendRepository closeFriendRepository;
    ProducerService producerService;

    private void publishNotifications(
            String eventId,
            Integer authorId,
            Integer postId,
            String privacy,
            String notificationType,
            String title,
            String message) {
        if (eventId == null || eventId.isBlank() || authorId == null || postId == null
                || privacy == null || privacy.isBlank()) {
            throw new IllegalArgumentException("Post event is missing required fields.");
        }

        Set<Integer> recipientIds = switch (privacy) {
            case "EVERYONE", "FOLLOWERS" -> this.getFollowers(authorId);
            case "BEST_FRIENDS" -> this.getCloseFriends(authorId);
            case "NO_ONE" -> Set.of();
            default -> throw new IllegalArgumentException("Unsupported post privacy: " + privacy);
        };

        recipientIds.stream()
                .filter(recipientId -> !recipientId.equals(authorId))
                .forEach(recipientId -> this.producerService.publishWithEventId(
                        eventId,
                        recipientId,
                        authorId,
                        notificationType,
                        "POST",
                        postId,
                        null,
                        title,
                        message,
                        Map.of("postId", postId, "route", "/posts/" + postId)));
    }

    private Set<Integer> getFollowers(Integer userId) {
        Set<Integer> recipientIds = new HashSet<>();
        int pageNumber = 0;
        Page<FollowEntity> page;
        do {
            page = this.followRepository.findAllByFollowingId(userId, PageRequest.of(pageNumber++, 500));
            page.getContent().stream().map(FollowEntity::getFollowerId).forEach(recipientIds::add);
        } while (page.hasNext());
        return recipientIds;
    }

    private Set<Integer> getCloseFriends(Integer userId) {
        Set<Integer> recipientIds = new HashSet<>();
        int pageNumber = 0;
        Page<CloseFriendEntity> page;
        do {
            page = this.closeFriendRepository.findAllByOwnerUserId(userId, PageRequest.of(pageNumber++, 500));
            page.getContent().stream().map(CloseFriendEntity::getMemberUserId).forEach(recipientIds::add);
        } while (page.hasNext());
        return recipientIds;
    }

    @Override
    @KafkaListener(topics = "post_published", containerFactory = "postPublishedTopicListenerFactory")
    public void onPostPublished(PostPublishedEventDTO event) {
        if (event == null) {
            throw new IllegalArgumentException("Post published event is required.");
        }
        this.publishNotifications(
                event.getEventId(), event.getAuthorId(), event.getPostId(), event.getPrivacy(),
                "POST_CREATE_NEW", "Bài viết mới", "đã đăng bài viết mới.");
    }

    @Override
    @KafkaListener(topics = "post_updated", containerFactory = "postUpdatedTopicListenerFactory")
    public void onPostUpdated(PostUpdatedEventDTO event) {
        if (event == null) {
            throw new IllegalArgumentException("Post updated event is required.");
        }
        this.publishNotifications(
                event.getEventId(), event.getAuthorId(), event.getPostId(), event.getPrivacy(),
                "POST_UPDATED", "Bài viết được cập nhật", "đã cập nhật bài viết.");
    }

}
