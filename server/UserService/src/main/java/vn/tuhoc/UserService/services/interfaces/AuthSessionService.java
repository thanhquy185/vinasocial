package vn.tuhoc.UserService.services.interfaces;

import java.util.List;

import vn.tuhoc.UserService.domains.entities.AuthSessionEntity;
import vn.tuhoc.UserService.dtos.requests.AuthSessionCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.AuthSessionDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.AuthSessionSummaryResponseDTO;

public interface AuthSessionService {
    AuthSessionEntity handleGetValidSessionByUserIdAndInstallationId(
            Integer userId,
            String installationId,
            String deviceName);

    AuthSessionEntity handleGetValidSessionByRefreshToken(String refreshToken);

    AuthSessionDetailResponseDTO handleCreate(AuthSessionCreateRequestDTO request);

    List<AuthSessionSummaryResponseDTO> handleListSessions(Integer userId, String currentSessionDeviceId);

    AuthSessionSummaryResponseDTO handleRevokeSession(Integer userId, String sessionDeviceId);

    AuthSessionSummaryResponseDTO handleLinkE2eeDevice(
            Integer userId,
            String currentSessionDeviceId,
            String e2eeDeviceId);

    void handleRevokeByUserId(Integer userId);
}
