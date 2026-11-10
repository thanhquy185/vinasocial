package vn.tuhoc.UserService.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import vn.tuhoc.UserService.domains.entities.AuthSessionEntity;
import vn.tuhoc.UserService.domains.entities.ProfileEntity;
import vn.tuhoc.UserService.domains.entities.UserEntity;
import vn.tuhoc.UserService.domains.enums.AuthOtpPurposeEnum;
import vn.tuhoc.UserService.domains.enums.UserRoleEnum;
import vn.tuhoc.UserService.domains.enums.UserStatusEnum;
import vn.tuhoc.UserService.domains.enums.UsernameTypeEnum;
import vn.tuhoc.UserService.exceptions.AuthMfaSettingException;
import vn.tuhoc.UserService.exceptions.AuthOtpInvalidCodeException;
import vn.tuhoc.UserService.dtos.events.AuthOtpRegisterByEmailEventDTO;
import vn.tuhoc.UserService.dtos.events.AuthOtpRegisterByPhoneEventDTO;
import vn.tuhoc.UserService.dtos.requests.AuthLoginRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthChangePasswordRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthConfirmPasswordChangeRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthMfaPasswordRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthOtpVerifyRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthOtpCreateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthRegisterRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthSessionCreateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.ProfileCreateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.UserCreateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.UserChangePasswordRequestDTO;
import vn.tuhoc.UserService.dtos.requests.UsernameCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.AuthLoginResponseDTO;
import vn.tuhoc.UserService.dtos.responses.AuthOtpDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.AuthOtpChallengeResponseDTO;
import vn.tuhoc.UserService.dtos.responses.AuthOtpVerificationResponseDTO;
import vn.tuhoc.UserService.dtos.responses.AuthMfaStatusResponseDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.UserDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.UsernameDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.AuthUserLoginResponseDTO;
import vn.tuhoc.UserService.dtos.responses.AuthSessionSummaryResponseDTO;
import vn.tuhoc.UserService.services.interfaces.AuthOtpService;
import vn.tuhoc.UserService.services.interfaces.AuthService;
import vn.tuhoc.UserService.services.interfaces.AuthSessionService;
import vn.tuhoc.UserService.services.interfaces.ProducerService;
import vn.tuhoc.UserService.services.interfaces.ProfileService;
import vn.tuhoc.UserService.services.interfaces.UserService;
import vn.tuhoc.UserService.services.interfaces.UsernameService;
import vn.tuhoc.UserService.utils.IdentifierUtil;
import vn.tuhoc.UserService.utils.LocalSecurityUtil;
import vn.tuhoc.core.utils.TimeUtil;
import vn.tuhoc.security.utils.SecurityUtil;

@Service
@RequiredArgsConstructor
@Transactional(noRollbackFor = AuthOtpInvalidCodeException.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImplement implements AuthService {
        @NonFinal
        @Value("${jwt.refresh-token-validity-in-seconds}")
        Long jwtRefreshTokenExpiration;
        AuthenticationManager authenticationManager;
        LocalSecurityUtil securityUtil;
        ProducerService producerService;
        UserService userService;
        UsernameService usernameService;
        ProfileService profileService;
        AuthSessionService authSessionService;
        AuthOtpService authOtpService;

        @Override
        public AuthOtpChallengeResponseDTO handleStartPasswordChange(AuthChangePasswordRequestDTO request) {
                this.verifyCurrentPassword(request.getCurrentPassword());
                UserEntity currentUser = this.getCurrentUser();
                return this.toChallenge(this.authOtpService.handleCreateSecurityOtp(
                                currentUser.getId(), AuthOtpPurposeEnum.CHANGE_PASSWORD));
        }

        @Override
        public UserDetailResponseDTO handleConfirmPasswordChange(AuthConfirmPasswordChangeRequestDTO request) {
                UserEntity currentUser = this.getCurrentUser();
                this.authOtpService.handleVerifyForPurpose(
                                new AuthOtpVerifyRequestDTO(request.getPublicId(), request.getCode(), null, null),
                                AuthOtpPurposeEnum.CHANGE_PASSWORD, currentUser.getId());
                return this.userService.handleChangePassword(currentUser.getId(),
                                UserChangePasswordRequestDTO.builder()
                                                .newPassword(request.getNewPassword())
                                                .newPassword2(request.getConfirmPassword())
                                                .build());
        }

        @Override
        public UserDetailResponseDTO handleRegister(AuthRegisterRequestDTO authRegisterRequestDTO) {
                // Tạo người dùng mới (mật khẩu sẽ được mã hoá trong User Service)
                UserCreateRequestDTO userCreateRequestDTO = UserCreateRequestDTO.builder()
                                .username(authRegisterRequestDTO.getIdentifier())
                                .password(authRegisterRequestDTO.getPassword())
                                .role(UserRoleEnum.MEMBER)
                                .status(UserStatusEnum.ACTIVE)
                                .build();
                UserDetailResponseDTO userDetailResponseDTO = this.userService.handleCreateMember(userCreateRequestDTO);

                // Tạo tên người dùng
                UsernameCreateRequestDTO usernameCreateRequestDTO = UsernameCreateRequestDTO.builder()
                                .userId(userDetailResponseDTO.getId())
                                .type(IdentifierUtil.isEmail(authRegisterRequestDTO.getIdentifier())
                                                ? UsernameTypeEnum.EMAIL
                                                : UsernameTypeEnum.PHONE)
                                .value(authRegisterRequestDTO.getIdentifier())
                                .isVerify(false)
                                .isActive(true)
                                .build();
                UsernameDetailResponseDTO usernameDetailResponseDTO = this.usernameService
                                .handleCreate(usernameCreateRequestDTO);
                userDetailResponseDTO.setUsernames(List.of(usernameDetailResponseDTO));

                // Tạo thông tin cho thành viên sử dụng
                ProfileCreateRequestDTO profileCreateRequestDTO = ProfileCreateRequestDTO.builder()
                                .userId(userDetailResponseDTO.getId())
                                .firstName(authRegisterRequestDTO.getFirstName())
                                .lastName(authRegisterRequestDTO.getLastName())
                                .birthdate(authRegisterRequestDTO.getBirthdate())
                                .gender(authRegisterRequestDTO.getGender())
                                .phone(IdentifierUtil.isPhone(authRegisterRequestDTO.getIdentifier())
                                        ? authRegisterRequestDTO.getIdentifier()
                                        : null)
                                .email(IdentifierUtil.isEmail(authRegisterRequestDTO.getIdentifier())
                                        ? authRegisterRequestDTO.getIdentifier()
                                        : null)
                                .username(authRegisterRequestDTO.getUsername())
                                .build();
                ProfileDetailResponseDTO profileDetailResponseDTO = this.profileService
                                .handleCreate(profileCreateRequestDTO);
                userDetailResponseDTO.setProfile(profileDetailResponseDTO);

                // Tạo xác thực otp
                AuthOtpCreateRequestDTO authOtpCreateRequestDTO = AuthOtpCreateRequestDTO.builder()
                                .userId(userDetailResponseDTO.getId())
                                .usernameId(userDetailResponseDTO.getUsernames().get(0).getId())
                                .purpose(AuthOtpPurposeEnum.REGISTER)
                                .build();
                AuthOtpDetailResponseDTO authOtpDetailResponseDTO = this.authOtpService
                                .handleCreate(authOtpCreateRequestDTO);
                userDetailResponseDTO.setAuthOtp(authOtpDetailResponseDTO);

                // Gửi event cho Notification Service
                // - Email
                if (IdentifierUtil.isEmail(authRegisterRequestDTO.getIdentifier())) {
                        AuthOtpRegisterByEmailEventDTO authOtpRegisterByEmailEventDTO = AuthOtpRegisterByEmailEventDTO
                                        .builder()
                                        .userId(userDetailResponseDTO.getId())
                                        .expiredAt(authOtpDetailResponseDTO.getExpiredAt())
                                        .code(authOtpDetailResponseDTO.getCode())
                                        .email(userDetailResponseDTO.getUsernames().get(0).getValue())
                                        .firstName(profileDetailResponseDTO.getFirstName())
                                        .lastName(profileDetailResponseDTO.getLastName())
                                        .build();
                        this.producerService.sendAuthOtpRegisterByEmailEvent(authOtpRegisterByEmailEventDTO);
                }
                // - Phone
                if (IdentifierUtil.isPhone(authRegisterRequestDTO.getIdentifier())) {
                        AuthOtpRegisterByPhoneEventDTO authOtpRegisterByPhoneEventDTO = AuthOtpRegisterByPhoneEventDTO
                                        .builder()
                                        .userId(userDetailResponseDTO.getId())
                                        .expiredAt(authOtpDetailResponseDTO.getExpiredAt())
                                        .code(authOtpDetailResponseDTO.getCode())
                                        .phone(userDetailResponseDTO.getUsernames().get(0).getValue())
                                        .firstName(profileDetailResponseDTO.getFirstName())
                                        .lastName(profileDetailResponseDTO.getLastName())
                                        .build();
                        this.producerService.sendAuthOtpRegisterByPhoneEvent(authOtpRegisterByPhoneEventDTO);
                }

                return userDetailResponseDTO;
        }

        @Override
        public UserDetailResponseDTO handleGetInfo() {
                String username = SecurityUtil.getCurrentUserLogin().isPresent()
                                ? SecurityUtil.getCurrentUserLogin().get()
                                : null;
                UserDetailResponseDTO currentUser = this.userService.handleGetByUsername(username);

                return currentUser;
        }

        @Override
        public AuthLoginResponseDTO handleLogin(AuthLoginRequestDTO authLoginRequestDTO) {
                // Lấy ra tên tài khoản, mật khẩu và tạo thông tin đăng nhập
                String identifierRequest = authLoginRequestDTO.getIdentifier();
                String passwordRequest = authLoginRequestDTO.getPassword();
                UserEntity currentUser = this.userService.handleGetByUsernameToLogin(identifierRequest);

                // Nạp input vào security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                identifierRequest, passwordRequest);

                // Xác thực người dùng
                Authentication authentication = this.authenticationManager
                                .authenticate(authenticationToken);

                if (Boolean.TRUE.equals(currentUser.getTwoFactorEnabled())) {
                        SecurityContextHolder.clearContext();
                        AuthOtpDetailResponseDTO otp = this.authOtpService.handleCreateSecurityOtp(
                                        currentUser.getId(), AuthOtpPurposeEnum.LOGIN);
                        return AuthLoginResponseDTO.builder()
                                        .mfaRequired(true)
                                        .otpPublicId(otp.getPublicId())
                                        .otpExpiresAt(otp.getExpiredAt())
                                        .otpDestination(this.maskIdentifier(otp.getUsername().getValue()))
                                        .build();
                }

                SecurityContextHolder.getContext().setAuthentication(authentication);
                return this.createLoginResponse(
                                currentUser,
                                identifierRequest,
                                authLoginRequestDTO.getInstallationId(),
                                authLoginRequestDTO.getDeviceName());
        }

        @Override
        public AuthLoginResponseDTO handleVerifyLoginOtp(AuthOtpVerifyRequestDTO request) {
                AuthOtpVerificationResponseDTO verification = this.authOtpService.handleVerifyForPurpose(
                                request, AuthOtpPurposeEnum.LOGIN, null);

                UserEntity user = this.userService.handleGetByUsernameToLogin(verification.getUsername());
                if (!Boolean.TRUE.equals(user.getTwoFactorEnabled())) {
                        throw new AuthMfaSettingException("Xác thực hai lớp đã bị tắt trong khi đăng nhập.");
                }

                return this.createLoginResponse(
                                user,
                                verification.getUsername(),
                                request.getInstallationId(),
                                request.getDeviceName());
        }

        private AuthLoginResponseDTO createLoginResponse(
                        UserEntity currentUser,
                        String username,
                        String requestedInstallationId,
                        String requestedDeviceName) {
                String installationId = requestedInstallationId == null || requestedInstallationId.isBlank()
                                ? UUID.randomUUID().toString()
                                : requestedInstallationId;
                String deviceName = requestedDeviceName == null || requestedDeviceName.isBlank()
                                ? "Trình duyệt web"
                                : requestedDeviceName.trim();
                AuthLoginResponseDTO restLogin = AuthLoginResponseDTO.builder()
                                .installationId(installationId)
                                .deviceName(deviceName)
                                .userLogin(AuthUserLoginResponseDTO.builder()
                                                .id(currentUser.getId())
                                                .publicId(currentUser.getPublicId())
                                                .role(currentUser.getRole())
                                                .username(this.getDisplayUsername(currentUser, username))
                                                .status(currentUser.getStatus())
                                                .build())
                                .build();

                AuthSessionEntity authSessionEntity = this.authSessionService
                                .handleGetValidSessionByUserIdAndInstallationId(
                                                currentUser.getId(),
                                                installationId,
                                                deviceName);
                String refreshToken;
                if (authSessionEntity != null) {
                        restLogin.setSessionDeviceId(authSessionEntity.getSessionDeviceId());
                        restLogin.setE2eeDeviceId(authSessionEntity.getE2eeDeviceId());
                        refreshToken = this.securityUtil.createRefreshToken(username, restLogin);
                        LocalDateTime currentDateTime = LocalDateTime.now();
                        authSessionEntity.setRefreshToken(refreshToken);
                        authSessionEntity.setExpiredAt(TimeUtil.getDatetime(
                                        currentDateTime.plusSeconds(this.jwtRefreshTokenExpiration)));
                } else {
                        restLogin.setSessionDeviceId(UUID.randomUUID().toString());
                        refreshToken = this.securityUtil.createRefreshToken(username, restLogin);
                        LocalDateTime currentDateTime = LocalDateTime.now();
                        AuthSessionCreateRequestDTO createRequest = AuthSessionCreateRequestDTO.builder()
                                        .userId(currentUser.getId())
                                        .username(username)
                                        .createdAt(TimeUtil.getDatetime(currentDateTime))
                                        .expiredAt(TimeUtil.getDatetime(
                                                        currentDateTime.plusSeconds(this.jwtRefreshTokenExpiration)))
                                        .installationId(installationId)
                                        .sessionDeviceId(restLogin.getSessionDeviceId())
                                        .deviceName(deviceName)
                                        .lastSeenAt(TimeUtil.getDatetime(currentDateTime))
                                        .refreshToken(refreshToken)
                                        .build();
                        this.authSessionService.handleCreate(createRequest);
                }
                restLogin.setAccessToken(this.securityUtil.createAccessToken(username, restLogin));
                restLogin.setResponseCookie(this.createRefreshCookie(refreshToken));
                return restLogin;
        }

        @Override
        public List<AuthSessionSummaryResponseDTO> handleListSessions() {
                UserEntity user = this.getCurrentUser();
                return this.authSessionService.handleListSessions(
                                user.getId(),
                                this.securityUtil.getCurrentSessionDeviceId());
        }

        @Override
        public AuthSessionSummaryResponseDTO handleRevokeSession(String sessionDeviceId) {
                UserEntity user = this.getCurrentUser();
                return this.authSessionService.handleRevokeSession(user.getId(), sessionDeviceId);
        }

        @Override
        public AuthSessionSummaryResponseDTO handleLinkCurrentSessionE2eeDevice(String e2eeDeviceId) {
                UserEntity user = this.getCurrentUser();
                return this.authSessionService.handleLinkE2eeDevice(
                                user.getId(),
                                this.securityUtil.getCurrentSessionDeviceId(),
                                e2eeDeviceId);
        }

        private ResponseCookie createRefreshCookie(String refreshToken) {
                return ResponseCookie.from("refreshToken", refreshToken)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .sameSite("None")
                                .maxAge(this.jwtRefreshTokenExpiration)
                                .build();
        }

        private String getDisplayUsername(UserEntity user, String fallback) {
                ProfileEntity profile = user.getProfile();
                return profile != null && profile.getUsername() != null
                                ? profile.getUsername()
                                : fallback;
        }

        @Override
        public AuthMfaStatusResponseDTO handleGetMfaStatus() {
                UserEntity user = this.getCurrentUser();
                return AuthMfaStatusResponseDTO.builder()
                                .enabled(Boolean.TRUE.equals(user.getTwoFactorEnabled()))
                                .build();
        }

        @Override
        public AuthOtpChallengeResponseDTO handleStartMfaEnable(AuthMfaPasswordRequestDTO request) {
                UserEntity user = this.getCurrentUser();
                if (Boolean.TRUE.equals(user.getTwoFactorEnabled())) {
                        throw new AuthMfaSettingException("Xác thực hai lớp đã được bật.");
                }

                this.verifyCurrentPassword(request.getPassword());

                return this.toChallenge(this.authOtpService.handleCreateSecurityOtp(
                                user.getId(), AuthOtpPurposeEnum.ENABLE_MFA));
        }

        @Override
        public AuthMfaStatusResponseDTO handleVerifyMfaEnable(AuthOtpVerifyRequestDTO request) {
                UserEntity user = this.getCurrentUser();

                this.authOtpService.handleVerifyForPurpose(request, AuthOtpPurposeEnum.ENABLE_MFA, user.getId());

                user.setTwoFactorEnabled(true);

                this.authSessionService.handleRevokeByUserId(user.getId());

                return AuthMfaStatusResponseDTO.builder().enabled(true).build();
        }

        @Override
        public AuthOtpChallengeResponseDTO handleStartMfaDisable(AuthMfaPasswordRequestDTO request) {
                UserEntity user = this.getCurrentUser();
                if (!Boolean.TRUE.equals(user.getTwoFactorEnabled())) {
                        throw new AuthMfaSettingException("Xác thực hai lớp hiện đang tắt.");
                }

                this.verifyCurrentPassword(request.getPassword());

                return this.toChallenge(this.authOtpService.handleCreateSecurityOtp(
                                user.getId(), AuthOtpPurposeEnum.DISABLE_MFA));
        }

        @Override
        public AuthMfaStatusResponseDTO handleVerifyMfaDisable(AuthOtpVerifyRequestDTO request) {
                UserEntity user = this.getCurrentUser();

                this.authOtpService.handleVerifyForPurpose(request, AuthOtpPurposeEnum.DISABLE_MFA, user.getId());

                user.setTwoFactorEnabled(false);

                return AuthMfaStatusResponseDTO.builder().enabled(false).build();
        }

        private UserEntity getCurrentUser() {
                String username = SecurityUtil.getCurrentUserLogin().orElseThrow(
                                () -> new IllegalStateException("Không tìm thấy phiên đăng nhập hiện tại."));
                return this.userService.handleGetByUsernameToLogin(username);
        }

        private void verifyCurrentPassword(String password) {
                String username = SecurityUtil.getCurrentUserLogin().orElseThrow(
                                () -> new IllegalStateException("Không tìm thấy phiên đăng nhập hiện tại."));
                try {
                        this.authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(username, password));
                } catch (AuthenticationException exception) {
                        throw new AuthMfaSettingException("Mật khẩu hiện tại không chính xác.");
                }
        }

        private AuthOtpChallengeResponseDTO toChallenge(AuthOtpDetailResponseDTO otp) {
                return AuthOtpChallengeResponseDTO.builder()
                                .publicId(otp.getPublicId())
                                .destination(this.maskIdentifier(otp.getUsername().getValue()))
                                .createdAt(otp.getCreatedAt())
                                .expiredAt(otp.getExpiredAt())
                                .purpose(otp.getPurpose())
                                .build();
        }

        private String maskIdentifier(String value) {
                if (value == null) {
                        return "";
                }
                int at = value.indexOf('@');
                if (at > 0) {
                        String local = value.substring(0, at);
                        String domain = value.substring(at + 1);
                        int dot = domain.lastIndexOf('.');
                        String domainName = dot > 0 ? domain.substring(0, dot) : domain;
                        String suffix = dot > 0 ? domain.substring(dot) : "";
                        return local.substring(0, 1) + "***@" + domainName.substring(0, 1) + "***" + suffix;
                }
                String digits = value.replaceAll("\\D", "");
                return "***" + (digits.length() > 3 ? digits.substring(digits.length() - 3) : digits);
        }

        @Override
        public AuthLoginResponseDTO handleRefreshToken(String refreshToken) {
                // Truy auth session từ refresh token để lấy ra thông tin tài khoản
                AuthSessionEntity currentAuthSession = this.authSessionService
                                .handleGetValidSessionByRefreshToken(refreshToken);
                String currentUsername = currentAuthSession.getUsername();
                UserEntity currentUser = currentAuthSession.getUser();

                // Tạo Rest Login DTO
                AuthLoginResponseDTO restLogin = AuthLoginResponseDTO.builder()
                                .sessionDeviceId(currentAuthSession.getSessionDeviceId())
                                .installationId(currentAuthSession.getInstallationId())
                                .deviceName(currentAuthSession.getDeviceName())
                                .e2eeDeviceId(currentAuthSession.getE2eeDeviceId())
                                .userLogin(AuthUserLoginResponseDTO.builder()
                                                .id(currentUser.getId())
                                                .publicId(currentUser.getPublicId())
                                                .role(currentUser.getRole())
                                                .username(this.getDisplayUsername(currentUser, currentUsername))
                                                .status(currentUser.getStatus())
                                                .build())
                                .build();

                // Tạo access token mới
                String newAccessToken = this.securityUtil.createAccessToken(currentUsername, restLogin);
                restLogin.setAccessToken(newAccessToken);

                String newRefreshToken = currentAuthSession.getRefreshToken();

                ResponseCookie responseCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                                .httpOnly(true) // Chỉ cho phép phía server được sử dụng (tạm cho client-web)
                                .secure(true) // Chỉ cho phép https
                                .path("/") // Cho phép tất cả đường dẫn
                                .sameSite("None") // quan trọng để cookie gửi qua cross-site
                                .maxAge(this.jwtRefreshTokenExpiration) //
                                .build();
                restLogin.setResponseCookie(responseCookie);

                return restLogin;
        }

        @Override
        public AuthLoginResponseDTO handleLogout(String refreshToken) {
                // Truy auth session từ refresh token để lấy ra thông tin tài khoản
                AuthSessionEntity currentAuthSession = this.authSessionService
                                .handleGetValidSessionByRefreshToken(refreshToken);
                currentAuthSession.setRevokedAt(TimeUtil.getCurrentDatetime());

                ResponseCookie responseCookie = ResponseCookie.from("refreshToken", null)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                AuthLoginResponseDTO restLogin = AuthLoginResponseDTO.builder()
                                .responseCookie(responseCookie)
                                .build();

                return restLogin;
        }

}
