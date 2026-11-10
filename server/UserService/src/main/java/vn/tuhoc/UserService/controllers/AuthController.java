package vn.tuhoc.UserService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.dtos.requests.AuthLoginRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthChangePasswordRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthConfirmPasswordChangeRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthMfaPasswordRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthOtpVerifyRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthRegisterRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthSessionE2eeLinkRequestDTO;
import vn.tuhoc.UserService.dtos.responses.AuthLoginResponseDTO;
import vn.tuhoc.UserService.dtos.responses.AuthMfaStatusResponseDTO;
import vn.tuhoc.UserService.dtos.responses.AuthOtpChallengeResponseDTO;
import vn.tuhoc.UserService.dtos.responses.AuthSessionSummaryResponseDTO;
import vn.tuhoc.UserService.dtos.responses.UserDetailResponseDTO;
import vn.tuhoc.UserService.services.interfaces.AuthService;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
        AuthService authService;

        @PreAuthorize("isAuthenticated()")
        @PostMapping("/password/challenge")
        public ResponseEntity<RestResponseDTO<AuthOtpChallengeResponseDTO>> startPasswordChange(
                        @RequestBody @Valid AuthChangePasswordRequestDTO request) {
                return RestResponseUtil.ok("Đã gửi mã xác thực để đổi mật khẩu.",
                                this.authService.handleStartPasswordChange(request));
        }

        @PreAuthorize("isAuthenticated()")
        @PostMapping("/password/verify")
        public ResponseEntity<RestResponseDTO<UserDetailResponseDTO>> confirmPasswordChange(
                        @RequestBody @Valid AuthConfirmPasswordChangeRequestDTO request) {
                return RestResponseUtil.ok("Đổi mật khẩu thành công.",
                                this.authService.handleConfirmPasswordChange(request));
        }

        @PreAuthorize("isAuthenticated()")
        @GetMapping("/sessions")
        public ResponseEntity<RestResponseDTO<List<AuthSessionSummaryResponseDTO>>> getSessions() {
                return RestResponseUtil.ok("Truy vấn các phiên đăng nhập thành công.",
                                this.authService.handleListSessions());
        }

        @PreAuthorize("isAuthenticated()")
        @DeleteMapping("/sessions/{sessionDeviceId}")
        public ResponseEntity<RestResponseDTO<AuthSessionSummaryResponseDTO>> revokeSession(
                        @PathVariable String sessionDeviceId) {
                return RestResponseUtil.ok("Đã thu hồi phiên đăng nhập trên thiết bị.",
                                this.authService.handleRevokeSession(sessionDeviceId));
        }

        @PreAuthorize("isAuthenticated()")
        @PutMapping("/sessions/me/e2ee-device")
        public ResponseEntity<RestResponseDTO<AuthSessionSummaryResponseDTO>> linkCurrentSessionE2eeDevice(
                        @RequestBody @Valid AuthSessionE2eeLinkRequestDTO request) {
                return RestResponseUtil.ok("Đã liên kết thiết bị E2EE với phiên đăng nhập.",
                                this.authService.handleLinkCurrentSessionE2eeDevice(request.getE2eeDeviceId()));
        }

        @PostMapping("/register")
        public ResponseEntity<RestResponseDTO<UserDetailResponseDTO>> register(
                        @RequestBody @Valid AuthRegisterRequestDTO authRegisterRequestDTO) {
                UserDetailResponseDTO userDetailResponseDTO = this.authService.handleRegister(authRegisterRequestDTO);

                return RestResponseUtil.created(
                                "Đăng ký tài khoản thành công!",
                                userDetailResponseDTO);
        }

        @PostMapping("/get-info")
        public ResponseEntity<RestResponseDTO<UserDetailResponseDTO>> getInfo() {
                UserDetailResponseDTO info = this.authService.handleGetInfo();

                return RestResponseUtil.ok(
                                "Truy thông tin tài khoản thành công!",
                                info);
        }

        @PostMapping("/login")
        public ResponseEntity<RestResponseDTO<AuthLoginResponseDTO>> login(
                        @RequestBody @Valid AuthLoginRequestDTO authLoginRequestDTO) {
                AuthLoginResponseDTO restLogin = this.authService.handleLogin(authLoginRequestDTO);
                if (restLogin.isMfaRequired()) {
                        return RestResponseUtil.ok("Đã gửi mã OTP để hoàn tất đăng nhập.", restLogin);
                }

                return RestResponseUtil.okWithCookie(
                                restLogin.getResponseCookie().toString(),
                                "Đăng nhập tài khoản thành công!",
                                restLogin);
        }

        @PostMapping("/login/verify-otp")
        public ResponseEntity<RestResponseDTO<AuthLoginResponseDTO>> verifyLoginOtp(
                        @RequestBody @Valid AuthOtpVerifyRequestDTO request) {
                AuthLoginResponseDTO restLogin = this.authService.handleVerifyLoginOtp(request);
                return RestResponseUtil.okWithCookie(
                                restLogin.getResponseCookie().toString(),
                                "Đăng nhập tài khoản thành công!",
                                restLogin);
        }

        @PreAuthorize("isAuthenticated()")
        @GetMapping("/mfa")
        public ResponseEntity<RestResponseDTO<AuthMfaStatusResponseDTO>> getMfaStatus() {
                return RestResponseUtil.ok("Truy vấn trạng thái xác thực hai lớp thành công.",
                                this.authService.handleGetMfaStatus());
        }

        @PreAuthorize("isAuthenticated()")
        @PostMapping("/mfa/enable")
        public ResponseEntity<RestResponseDTO<AuthOtpChallengeResponseDTO>> startMfaEnable(
                        @RequestBody @Valid AuthMfaPasswordRequestDTO request) {
                return RestResponseUtil.ok("Đã gửi mã xác nhận để bật xác thực hai lớp.",
                                this.authService.handleStartMfaEnable(request));
        }

        @PreAuthorize("isAuthenticated()")
        @PostMapping("/mfa/enable/verify")
        public ResponseEntity<RestResponseDTO<AuthMfaStatusResponseDTO>> verifyMfaEnable(
                        @RequestBody @Valid AuthOtpVerifyRequestDTO request) {
                return RestResponseUtil.ok("Đã bật xác thực hai lớp.",
                                this.authService.handleVerifyMfaEnable(request));
        }

        @PreAuthorize("isAuthenticated()")
        @PostMapping("/mfa/disable")
        public ResponseEntity<RestResponseDTO<AuthOtpChallengeResponseDTO>> startMfaDisable(
                        @RequestBody @Valid AuthMfaPasswordRequestDTO request) {
                return RestResponseUtil.ok("Đã gửi mã xác nhận để tắt xác thực hai lớp.",
                                this.authService.handleStartMfaDisable(request));
        }

        @PreAuthorize("isAuthenticated()")
        @PostMapping("/mfa/disable/verify")
        public ResponseEntity<RestResponseDTO<AuthMfaStatusResponseDTO>> verifyMfaDisable(
                        @RequestBody @Valid AuthOtpVerifyRequestDTO request) {
                return RestResponseUtil.ok("Đã tắt xác thực hai lớp.",
                                this.authService.handleVerifyMfaDisable(request));
        }

        @PostMapping("/refresh-token")
        public ResponseEntity<RestResponseDTO<AuthLoginResponseDTO>> refreshToken(
                        @CookieValue("refreshToken") String refreshToken) {
                AuthLoginResponseDTO restLogin = this.authService.handleRefreshToken(refreshToken);

                return RestResponseUtil.okWithCookie(
                                restLogin.getResponseCookie().toString(),
                                "Thay đổi refresh token tài khoản thành công!",
                                restLogin);
        }

        @PostMapping("/logout")
        public ResponseEntity<RestResponseDTO<Object>> logout(@CookieValue("refreshToken") String refreshToken) {
                AuthLoginResponseDTO restLogin = this.authService.handleLogout(refreshToken);

                return RestResponseUtil.okWithCookie(
                                restLogin.getResponseCookie().toString(),
                                "Đăng xuất tài khoản thành công!",
                                restLogin);
        }
}
