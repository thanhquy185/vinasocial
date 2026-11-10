package vn.tuhoc.UserService.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.tuhoc.UserService.domains.entities.AuthSessionEntity;

public interface AuthSessionRepository
                extends JpaRepository<AuthSessionEntity, Integer>, JpaSpecificationExecutor<AuthSessionEntity> {
        Optional<AuthSessionEntity> findByRefreshToken(String refreshToken);

        Optional<AuthSessionEntity> findFirstByUserIdAndInstallationIdAndRevokedAtIsNullOrderByCreatedAtDesc(
                        Integer userId,
                        String installationId);

        Optional<AuthSessionEntity> findByUserIdAndSessionDeviceId(Integer userId, String sessionDeviceId);

        List<AuthSessionEntity> findTop50ByUserIdOrderByLastSeenAtDesc(Integer userId);

        List<AuthSessionEntity> findAllByUserIdAndRevokedAtIsNull(Integer userId);
}
