package vn.tuhoc.UserService.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.tuhoc.UserService.domains.entities.ProfileLinkEntity;

public interface ProfileLinkRepository
                extends JpaRepository<ProfileLinkEntity, Integer>, JpaSpecificationExecutor<ProfileLinkEntity> {
}
