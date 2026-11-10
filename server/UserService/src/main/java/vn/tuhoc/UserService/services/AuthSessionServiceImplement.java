package vn.tuhoc.UserService.services;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.entities.AuthSessionEntity;
import vn.tuhoc.UserService.domains.mappers.AuthSessionMapper;
import vn.tuhoc.UserService.dtos.requests.AuthSessionCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.AuthSessionDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.AuthSessionSummaryResponseDTO;
import vn.tuhoc.UserService.exceptions.AuthAccessTokenIsNotValidException;
import vn.tuhoc.UserService.exceptions.AuthSessionNotFoundByDeviceIdException;
import vn.tuhoc.UserService.exceptions.AuthSessionNotFoundByRefreshTokenException;
import vn.tuhoc.UserService.repositories.AuthSessionRepository;
import vn.tuhoc.UserService.services.interfaces.AuthSessionService;
import vn.tuhoc.core.utils.TimeUtil;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthSessionServiceImplement implements AuthSessionService {
    AuthSessionRepository authSessionRepository;
    AuthSessionMapper authSessionMapper;

    private AuthSessionEntity getByRefreshToken(String refreshToken) {
        return this.authSessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(AuthSessionNotFoundByRefreshTokenException::new);
    }

    private boolean isExpired(AuthSessionEntity session) {
        return !TimeUtil.getLocalDateTime(session.getExpiredAt())
                .isAfter(TimeUtil.getLocalDateTime(TimeUtil.getCurrentDatetime()));
    }

    private AuthSessionEntity getValidSessionByRefreshToken(String refreshToken) {
        AuthSessionEntity session = this.getByRefreshToken(refreshToken);
        if (session.getRevokedAt() != null || this.isExpired(session)) {
            throw new AuthAccessTokenIsNotValidException();
        }
        return session;
    }

    private AuthSessionSummaryResponseDTO toSummary(AuthSessionEntity session, String currentSessionDeviceId) {
        boolean active = session.getRevokedAt() == null && !this.isExpired(session);
        return AuthSessionSummaryResponseDTO.builder()
                .sessionDeviceId(session.getSessionDeviceId())
                .installationId(session.getInstallationId())
                .deviceName(session.getDeviceName())
                .createdAt(session.getCreatedAt())
                .expiredAt(session.getExpiredAt())
                .lastSeenAt(session.getLastSeenAt())
                .revokedAt(session.getRevokedAt())
                .e2eeDeviceId(session.getE2eeDeviceId())
                .current(session.getSessionDeviceId() != null
                        && session.getSessionDeviceId().equals(currentSessionDeviceId))
                .active(active)
                .build();
    }

    @Override
    public AuthSessionEntity handleGetValidSessionByUserIdAndInstallationId(
            Integer userId,
            String installationId,
            String deviceName) {
        AuthSessionEntity session = this.authSessionRepository
                .findFirstByUserIdAndInstallationIdAndRevokedAtIsNullOrderByCreatedAtDesc(userId, installationId)
                .orElse(null);
        if (session == null) return null;

        if (this.isExpired(session)) {
            session.setRevokedAt(TimeUtil.getCurrentDatetime());
            this.authSessionRepository.save(session);
            return null;
        }

        session.setLastSeenAt(TimeUtil.getCurrentDatetime());
        session.setDeviceName(deviceName);
        return this.authSessionRepository.save(session);
    }

    @Override
    public AuthSessionEntity handleGetValidSessionByRefreshToken(String refreshToken) {
        AuthSessionEntity session = this.getValidSessionByRefreshToken(refreshToken);
        session.setLastSeenAt(TimeUtil.getCurrentDatetime());
        return this.authSessionRepository.save(session);
    }

    @Override
    public AuthSessionDetailResponseDTO handleCreate(AuthSessionCreateRequestDTO request) {
        AuthSessionEntity session = this.authSessionMapper.createEntityFromRequest(request);
        return this.authSessionMapper.entityToDetailResponse(this.authSessionRepository.save(session));
    }

    @Override
    public List<AuthSessionSummaryResponseDTO> handleListSessions(Integer userId, String currentSessionDeviceId) {
        return this.authSessionRepository.findTop50ByUserIdOrderByLastSeenAtDesc(userId).stream()
                .map(session -> this.toSummary(session, currentSessionDeviceId))
                .toList();
    }

    @Override
    public AuthSessionSummaryResponseDTO handleRevokeSession(Integer userId, String sessionDeviceId) {
        AuthSessionEntity session = this.authSessionRepository.findByUserIdAndSessionDeviceId(userId, sessionDeviceId)
                .orElseThrow(() -> new AuthSessionNotFoundByDeviceIdException(sessionDeviceId));
        if (session.getRevokedAt() == null) {
            session.setRevokedAt(TimeUtil.getCurrentDatetime());
            session = this.authSessionRepository.save(session);
        }
        return this.toSummary(session, null);
    }

    @Override
    public AuthSessionSummaryResponseDTO handleLinkE2eeDevice(
            Integer userId,
            String currentSessionDeviceId,
            String e2eeDeviceId) {
        if (currentSessionDeviceId == null || currentSessionDeviceId.isBlank()) {
            throw new AuthAccessTokenIsNotValidException();
        }
        AuthSessionEntity session = this.authSessionRepository
                .findByUserIdAndSessionDeviceId(userId, currentSessionDeviceId)
                .filter(value -> value.getRevokedAt() == null && !this.isExpired(value))
                .orElseThrow(AuthAccessTokenIsNotValidException::new);
        session.setE2eeDeviceId(e2eeDeviceId);
        session.setLastSeenAt(TimeUtil.getCurrentDatetime());
        return this.toSummary(this.authSessionRepository.save(session), currentSessionDeviceId);
    }

    @Override
    public void handleRevokeByUserId(Integer userId) {
        List<AuthSessionEntity> activeSessions = this.authSessionRepository.findAllByUserIdAndRevokedAtIsNull(userId);
        String revokedAt = TimeUtil.getCurrentDatetime();
        activeSessions.forEach(session -> session.setRevokedAt(revokedAt));

        this.authSessionRepository.saveAll(activeSessions);
    }
}
