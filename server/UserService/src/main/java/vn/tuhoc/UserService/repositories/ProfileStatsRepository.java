package vn.tuhoc.UserService.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.tuhoc.UserService.domains.entities.ProfileStatsEntity;

public interface ProfileStatsRepository
                extends JpaRepository<ProfileStatsEntity, Integer>, JpaSpecificationExecutor<ProfileStatsEntity> {
        Optional<ProfileStatsEntity> findByProfileId(Integer profileId);
}
