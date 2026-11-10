package vn.tuhoc.NotificationService.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import vn.tuhoc.NotificationService.domains.entities.NotificationPreferenceEntity;

public interface NotificationPreferenceRepository extends MongoRepository<NotificationPreferenceEntity, Integer> {
}
