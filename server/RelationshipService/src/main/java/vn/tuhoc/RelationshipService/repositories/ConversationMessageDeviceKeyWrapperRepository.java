package vn.tuhoc.RelationshipService.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.tuhoc.RelationshipService.domains.entities.ConversationMessageDeviceKeyWrapperEntity;

public interface ConversationMessageDeviceKeyWrapperRepository
        extends JpaRepository<ConversationMessageDeviceKeyWrapperEntity, Long> {
    List<ConversationMessageDeviceKeyWrapperEntity> findAllByMessageIdInAndDeviceIdIn(
            Collection<Integer> messageIds, Collection<String> deviceIds);

    Optional<ConversationMessageDeviceKeyWrapperEntity> findByMessageIdAndDeviceId(
            Integer messageId, String deviceId);
}
