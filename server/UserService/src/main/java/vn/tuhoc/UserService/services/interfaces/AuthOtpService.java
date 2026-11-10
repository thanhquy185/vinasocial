package vn.tuhoc.UserService.services.interfaces;

import vn.tuhoc.UserService.dtos.requests.AuthOtpResetPasswordRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthOtpCreateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthOtpForgotPasswordRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthOtpVerifyRequestDTO;
import vn.tuhoc.UserService.dtos.responses.AuthOtpDetailResponseDTO;
import vn.tuhoc.UserService.domains.enums.AuthOtpPurposeEnum;
import vn.tuhoc.UserService.dtos.responses.AuthOtpVerificationResponseDTO;

public interface AuthOtpService {
    AuthOtpDetailResponseDTO handleGetByPublicId(String publicId);

    AuthOtpDetailResponseDTO handleCreate(AuthOtpCreateRequestDTO authOtpCreateRequestDTO);

    AuthOtpDetailResponseDTO handleCreateSecurityOtp(Integer userId, AuthOtpPurposeEnum purpose);

    AuthOtpDetailResponseDTO handleForgotPassword(AuthOtpForgotPasswordRequestDTO authOtpForgotPasswordRequestDTO);

    void handleResetPassword(AuthOtpResetPasswordRequestDTO AuthOtpResetPasswordRequestDTO);

    AuthOtpDetailResponseDTO handleVerify(AuthOtpVerifyRequestDTO authOtpVerifyRequestDTO);

    AuthOtpVerificationResponseDTO handleVerifyForPurpose(
            AuthOtpVerifyRequestDTO authOtpVerifyRequestDTO,
            AuthOtpPurposeEnum expectedPurpose,
            Integer expectedUserId);

    AuthOtpDetailResponseDTO handleResend(String publicId);
}
