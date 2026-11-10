package vn.tuhoc.NotificationService.services.interfaces;

import vn.tuhoc.NotificationService.dtos.events.AuthOtpForgotPasswordByPhoneEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpRegisterByPhoneEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpSecurityEventDTO;

public interface SmsService {
    void sendRegisterByPhoneOtp(AuthOtpRegisterByPhoneEventDTO authOtpRegisterByPhoneEventDTO);

    void sendForgotPasswordByPhoneOtp(AuthOtpForgotPasswordByPhoneEventDTO authOtpForgotPasswordByPhoneEventDTO);

    void sendSecurityOtpByPhone(AuthOtpSecurityEventDTO event);
}
