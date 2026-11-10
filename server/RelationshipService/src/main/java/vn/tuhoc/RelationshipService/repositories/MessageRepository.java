package vn.tuhoc.RelationshipService.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.tuhoc.RelationshipService.domains.entities.MessageEntity;

public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {
    Page<MessageEntity> findAllByConversationId(Integer conversationId, Pageable pageable);

    Page<MessageEntity> findAllByConversationIdOrderByIdDesc(Integer conversationId, Pageable pageable);

    Page<MessageEntity> findAllByConversationIdAndIdLessThan(
            Integer conversationId, Integer beforeId, Pageable pageable);

    Page<MessageEntity> findAllByConversationIdAndIdLessThanOrderByIdDesc(
            Integer conversationId, Integer beforeId, Pageable pageable);

    Optional<MessageEntity> findByIdAndConversationId(Integer id, Integer conversationId);

    Optional<MessageEntity> findBySenderIdAndClientMessageId(Integer senderId, String clientMessageId);
}
