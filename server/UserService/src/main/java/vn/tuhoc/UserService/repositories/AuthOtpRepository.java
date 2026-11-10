package vn.tuhoc.UserService.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.tuhoc.UserService.domains.entities.AuthOtpEntity;
import vn.tuhoc.UserService.domains.enums.AuthOtpPurposeEnum;

public interface AuthOtpRepository
                extends JpaRepository<AuthOtpEntity, Integer>, JpaSpecificationExecutor<AuthOtpEntity> {
    Optional<AuthOtpEntity> findByPublicId(String publicId);

    Optional<AuthOtpEntity> findFirstByUserIdAndPurposeAndVerifiedAtIsNullOrderByCreatedAtDesc(
            Integer userId,
            AuthOtpPurposeEnum purpose);
}
