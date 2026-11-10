package vn.tuhoc.NotificationService.services.interfaces;

import vn.tuhoc.NotificationService.dtos.events.AuthOtpForgotPasswordByEmailEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpRegisterByEmailEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpSecurityEventDTO;

public interface EmailService {
    void sendRegisterByEmailOtp(AuthOtpRegisterByEmailEventDTO authOtpRegisterByEmailEventDTO);

    void sendForgotPasswordByEmailOtp(AuthOtpForgotPasswordByEmailEventDTO authOtpForgotPasswordByEmailEventDTO);

    void sendSecurityOtpByEmail(AuthOtpSecurityEventDTO event);
}
