package vn.tuhoc.UserService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import vn.tuhoc.UserService.dtos.requests.AuthOtpForgotPasswordRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthOtpResetPasswordRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthOtpVerifyRequestDTO;
import vn.tuhoc.UserService.dtos.responses.AuthOtpDetailResponseDTO;
import vn.tuhoc.UserService.services.interfaces.AuthOtpService;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestController
@RequestMapping("/api/v1/auth/otp")
@RequiredArgsConstructor 
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthOtpController {
    AuthOtpService authOtpService;

    @GetMapping("/{publicId}")
    public ResponseEntity<RestResponseDTO<AuthOtpDetailResponseDTO>> getByPublicId(
            @PathVariable("publicId") String publicId) {
        AuthOtpDetailResponseDTO authOtpDetailResponseDTO = this.authOtpService.handleGetByPublicId(publicId);

        return RestResponseUtil.ok("Truy vấn mã xác thực theo public id thành công!", authOtpDetailResponseDTO);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<RestResponseDTO<AuthOtpDetailResponseDTO>> forgotPassword(
            @Valid @RequestBody AuthOtpForgotPasswordRequestDTO authOtpForgotPasswordRequestDTO) {
        AuthOtpDetailResponseDTO authOtpDetailResponseDTO = this.authOtpService
                .handleForgotPassword(authOtpForgotPasswordRequestDTO);

        return RestResponseUtil.ok("Tạo mã xác thực otp khi để đặt lại mật khẩu thành công!", authOtpDetailResponseDTO);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<RestResponseDTO<Object>> resetPassword(
            @Valid @RequestBody AuthOtpResetPasswordRequestDTO authOtpResetPasswordRequestDTO) {
        this.authOtpService.handleResetPassword(authOtpResetPasswordRequestDTO);

        return RestResponseUtil.ok("Đặt lại mật khẩu thành công!", null);
    }

    @PostMapping("/verify")
    public ResponseEntity<RestResponseDTO<AuthOtpDetailResponseDTO>> verify(
            @Valid @RequestBody AuthOtpVerifyRequestDTO authOtpVerifyRequestDTO) {
        AuthOtpDetailResponseDTO authOtpDetailResponseDTO = this.authOtpService.handleVerify(authOtpVerifyRequestDTO);

        return RestResponseUtil.ok("Xác thực mã otp thành công!", authOtpDetailResponseDTO);
    }

    @PostMapping("/resend/{publicId}")
    public ResponseEntity<RestResponseDTO<AuthOtpDetailResponseDTO>> postMethodName(
            @PathVariable("publicId") String publicId) {
        AuthOtpDetailResponseDTO authOtpDetailResponseDTO = this.authOtpService.handleResend(publicId);

        return RestResponseUtil.created("Gửi lại mã xác thực mã otp thành công!", authOtpDetailResponseDTO);
    }

}
