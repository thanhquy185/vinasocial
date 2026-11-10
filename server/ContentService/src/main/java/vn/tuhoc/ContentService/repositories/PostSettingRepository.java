package vn.tuhoc.ContentService.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.tuhoc.ContentService.domains.entities.PostSettingEntity;

public interface PostSettingRepository
        extends JpaRepository<PostSettingEntity, Integer>, JpaSpecificationExecutor<PostSettingEntity> {

}
