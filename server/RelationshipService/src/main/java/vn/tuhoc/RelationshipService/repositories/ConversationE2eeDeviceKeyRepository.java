package vn.tuhoc.RelationshipService.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.tuhoc.RelationshipService.domains.entities.ConversationE2eeDeviceKeyEntity;

public interface ConversationE2eeDeviceKeyRepository
        extends JpaRepository<ConversationE2eeDeviceKeyEntity, String> {
    List<ConversationE2eeDeviceKeyEntity> findAllByUserIdInAndRevokedAtIsNull(Collection<Integer> userIds);

    List<ConversationE2eeDeviceKeyEntity> findAllByUserIdAndRevokedAtIsNull(Integer userId);

    List<ConversationE2eeDeviceKeyEntity> findAllByUserIdAndStatusAndRevokedAtIsNull(
            Integer userId, String status);

    Optional<ConversationE2eeDeviceKeyEntity> findFirstByUserIdAndAuthSessionDeviceIdAndStatusAndRevokedAtIsNull(
            Integer userId, String authSessionDeviceId, String status);

    List<ConversationE2eeDeviceKeyEntity> findAllByUserIdOrderByCreatedAtDesc(Integer userId);

    Optional<ConversationE2eeDeviceKeyEntity> findByDeviceIdAndUserId(
            String deviceId, Integer userId);
}
