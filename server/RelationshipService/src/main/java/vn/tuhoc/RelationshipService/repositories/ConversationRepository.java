package vn.tuhoc.RelationshipService.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;

import vn.tuhoc.RelationshipService.domains.entities.ConversationEntity;
import vn.tuhoc.RelationshipService.domains.enums.ConversationRequestStatusEnum;
import vn.tuhoc.RelationshipService.domains.enums.ConversationTypeEnum;

public interface ConversationRepository extends JpaRepository<ConversationEntity, Integer> {
    @Modifying
    @Query(value = "INSERT INTO conversations "
            + "(created_at, created_by_id, direct_user_low_id, direct_user_high_id, "
            + "last_activity_at, `type`, updated_at) "
            + "VALUES (:now, :creatorId, :lowId, :highId, :now, 'DIRECT', :now) "
            + "ON DUPLICATE KEY UPDATE id = id", nativeQuery = true)
    void ensureDirectConversation(
            @Param("creatorId") Integer creatorId,
            @Param("lowId") Integer lowId,
            @Param("highId") Integer highId,
            @Param("now") String now);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ConversationEntity c WHERE c.type = :type "
            + "AND c.directUserLowId = :lowId AND c.directUserHighId = :highId")
    Optional<ConversationEntity> findDirectConversationForUpdate(
            @Param("type") ConversationTypeEnum type,
            @Param("lowId") Integer lowId,
            @Param("highId") Integer highId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ConversationEntity c WHERE c.id = :conversationId")
    Optional<ConversationEntity> findByIdForUpdate(@Param("conversationId") Integer conversationId);

    @Query(value = "SELECT c FROM ConversationEntity c "
            + "WHERE c.id IN (SELECT p.conversationId FROM ConversationParticipantEntity p "
            + "WHERE p.userId = :userId AND p.leftAt IS NULL) "
            + "AND NOT EXISTS (SELECT r.id FROM ConversationRequestEntity r "
            + "WHERE r.conversationId = c.id AND r.status <> :acceptedStatus) "
            + "ORDER BY c.lastActivityAt DESC, c.id DESC",
            countQuery = "SELECT COUNT(c) FROM ConversationEntity c "
                    + "WHERE c.id IN (SELECT p.conversationId FROM ConversationParticipantEntity p "
                    + "WHERE p.userId = :userId AND p.leftAt IS NULL) "
                    + "AND NOT EXISTS (SELECT r.id FROM ConversationRequestEntity r "
                    + "WHERE r.conversationId = c.id AND r.status <> :acceptedStatus)")
    Page<ConversationEntity> findAllActiveByParticipantUserId(
            @Param("userId") Integer userId,
            @Param("acceptedStatus") ConversationRequestStatusEnum acceptedStatus,
            Pageable pageable);
}
