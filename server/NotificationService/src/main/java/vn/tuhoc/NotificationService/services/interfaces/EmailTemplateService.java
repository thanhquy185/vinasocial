package vn.tuhoc.NotificationService.services.interfaces;

import vn.tuhoc.NotificationService.dtos.events.AuthOtpForgotPasswordByEmailEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpRegisterByEmailEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpSecurityEventDTO;

public interface EmailTemplateService {
    String renderRegisterByEmailOtp(AuthOtpRegisterByEmailEventDTO authOtpRegisterByEmailEventDTO);

    String renderForgotPasswordByEmailOtp(AuthOtpForgotPasswordByEmailEventDTO authOtpForgotPasswordByEmailEventDTO);

    String renderSecurityOtp(AuthOtpSecurityEventDTO event);
}
