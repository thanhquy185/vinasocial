package vn.tuhoc.UserService.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.tuhoc.UserService.domains.entities.ProfilePrivacySettingEntity;

public interface ProfilePrivacySettingRepository extends JpaRepository<ProfilePrivacySettingEntity, Integer>,
                JpaSpecificationExecutor<ProfilePrivacySettingEntity> {
}
