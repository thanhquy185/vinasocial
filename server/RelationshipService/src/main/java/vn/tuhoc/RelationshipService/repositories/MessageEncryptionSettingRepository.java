package vn.tuhoc.RelationshipService.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.tuhoc.RelationshipService.domains.entities.MessageEncryptionSettingEntity;

public interface MessageEncryptionSettingRepository extends JpaRepository<MessageEncryptionSettingEntity, Integer> {
}
