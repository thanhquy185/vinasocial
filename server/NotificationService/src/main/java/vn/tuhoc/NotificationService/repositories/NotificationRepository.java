package vn.tuhoc.NotificationService.repositories;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.tuhoc.NotificationService.domains.entities.NotificationEntity;
import vn.tuhoc.NotificationService.domains.enums.NotificationStatusEnum;

public interface NotificationRepository extends MongoRepository<NotificationEntity, String> {
    Page<NotificationEntity> findAllByRecipientIdAndStatusIn(
            Integer recipientId,
            Collection<NotificationStatusEnum> statuses,
            Pageable pageable);

    Optional<NotificationEntity> findByIdAndRecipientId(String id, Integer recipientId);

    Optional<NotificationEntity> findByEventKey(String eventKey);

    long countByRecipientIdAndStatus(Integer recipientId, NotificationStatusEnum status);
}
