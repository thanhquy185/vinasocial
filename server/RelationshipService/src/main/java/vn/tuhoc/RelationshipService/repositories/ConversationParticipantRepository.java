package vn.tuhoc.RelationshipService.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.tuhoc.RelationshipService.domains.entities.ConversationParticipantEntity;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipantEntity, Integer> {
    boolean existsByConversationIdAndUserIdAndLeftAtIsNull(Integer conversationId, Integer userId);

    Optional<ConversationParticipantEntity> findByConversationIdAndUserId(Integer conversationId, Integer userId);

    List<ConversationParticipantEntity> findAllByConversationIdAndLeftAtIsNull(Integer conversationId);

    List<ConversationParticipantEntity> findAllByConversationIdAndLeftAtIsNullOrderByJoinedAtAsc(Integer conversationId);

    Page<ConversationParticipantEntity> findAllByUserIdAndLeftAtIsNull(Integer userId, Pageable pageable);

    List<ConversationParticipantEntity> findAllByConversationIdInAndUserId(
            Collection<Integer> conversationIds, Integer userId);
}
