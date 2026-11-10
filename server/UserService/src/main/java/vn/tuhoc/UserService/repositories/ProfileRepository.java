package vn.tuhoc.UserService.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.tuhoc.UserService.domains.entities.ProfileEntity;

public interface ProfileRepository
        extends JpaRepository<ProfileEntity, Integer>, JpaSpecificationExecutor<ProfileEntity> {
    Boolean existsByPhone(String phone);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByUsernameAndIdNot(String username, Integer id);

    Optional<ProfileEntity> findByUserId(Integer userId);

    Optional<ProfileEntity> findByPhone(String phone);

    Optional<ProfileEntity> findByEmail(String email);

    Optional<ProfileEntity> findByUsername(String username);
}
