package vn.tuhoc.UserService.services.interfaces;

import vn.tuhoc.UserService.dtos.requests.AuthLoginRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthChangePasswordRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthConfirmPasswordChangeRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthRegisterRequestDTO;
import vn.tuhoc.UserService.dtos.responses.AuthLoginResponseDTO;
import vn.tuhoc.UserService.dtos.responses.UserDetailResponseDTO;
import vn.tuhoc.UserService.dtos.requests.AuthMfaPasswordRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthOtpVerifyRequestDTO;
import vn.tuhoc.UserService.dtos.responses.AuthMfaStatusResponseDTO;
import vn.tuhoc.UserService.dtos.responses.AuthOtpChallengeResponseDTO;
import vn.tuhoc.UserService.dtos.responses.AuthSessionSummaryResponseDTO;
import java.util.List;

public interface AuthService {
    AuthOtpChallengeResponseDTO handleStartPasswordChange(AuthChangePasswordRequestDTO request);

    UserDetailResponseDTO handleConfirmPasswordChange(AuthConfirmPasswordChangeRequestDTO request);

    UserDetailResponseDTO handleRegister(AuthRegisterRequestDTO authRegisterRequestDTO);

    UserDetailResponseDTO handleGetInfo();

    AuthLoginResponseDTO handleLogin(AuthLoginRequestDTO authLoginRequestDTO);

    AuthLoginResponseDTO handleVerifyLoginOtp(AuthOtpVerifyRequestDTO request);

    List<AuthSessionSummaryResponseDTO> handleListSessions();

    AuthSessionSummaryResponseDTO handleRevokeSession(String sessionDeviceId);

    AuthSessionSummaryResponseDTO handleLinkCurrentSessionE2eeDevice(String e2eeDeviceId);

    AuthMfaStatusResponseDTO handleGetMfaStatus();

    AuthOtpChallengeResponseDTO handleStartMfaEnable(AuthMfaPasswordRequestDTO request);

    AuthMfaStatusResponseDTO handleVerifyMfaEnable(AuthOtpVerifyRequestDTO request);

    AuthOtpChallengeResponseDTO handleStartMfaDisable(AuthMfaPasswordRequestDTO request);

    AuthMfaStatusResponseDTO handleVerifyMfaDisable(AuthOtpVerifyRequestDTO request);

    AuthLoginResponseDTO handleRefreshToken(String refreshToken);

    AuthLoginResponseDTO handleLogout(String refreshToken);
}
