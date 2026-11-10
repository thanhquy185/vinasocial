package vn.tuhoc.RelationshipService.repositories;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.tuhoc.RelationshipService.domains.entities.ConversationRequestEntity;
import vn.tuhoc.RelationshipService.domains.enums.ConversationRequestStatusEnum;

public interface ConversationRequestRepository extends JpaRepository<ConversationRequestEntity, Integer> {
    Optional<ConversationRequestEntity> findByConversationId(Integer conversationId);

    Optional<ConversationRequestEntity> findByIdAndReceiverId(Integer id, Integer receiverId);

    Optional<ConversationRequestEntity> findByIdAndSenderId(Integer id, Integer senderId);

    List<ConversationRequestEntity> findAllBySenderIdAndReceiverIdAndStatus(
            Integer senderId, Integer receiverId, ConversationRequestStatusEnum status);

    Page<ConversationRequestEntity> findAllByReceiverIdAndStatus(
            Integer receiverId, ConversationRequestStatusEnum status, Pageable pageable);

    Page<ConversationRequestEntity> findAllBySenderIdAndStatus(
            Integer senderId, ConversationRequestStatusEnum status, Pageable pageable);
}
