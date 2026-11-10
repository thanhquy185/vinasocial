package vn.tuhoc.NotificationService.services.interfaces;

import vn.tuhoc.NotificationService.dtos.events.AuthOtpForgotPasswordByEmailEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpForgotPasswordByPhoneEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpRegisterByEmailEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpRegisterByPhoneEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpSecurityEventDTO;
import vn.tuhoc.NotificationService.dtos.events.NotificationCreatedEventDTO;
import vn.tuhoc.NotificationService.dtos.events.NotificationPreferencesUpdatedEventDTO;

public interface ConsumerService {
    void listenAuthOtpRegisterByEmailEvent(AuthOtpRegisterByEmailEventDTO authOtpRegisterByEmailEventDTO);

    void listenAuthOtpRegisterByEmailEventDlt(AuthOtpRegisterByEmailEventDTO authOtpRegisterByEmailEventDTO);

    void listenAuthOtpRegisterByPhoneEvent(AuthOtpRegisterByPhoneEventDTO authOtpRegisterByPhoneEventDTO);

    void listenAuthOtpRegisterByPhoneEventDlt(AuthOtpRegisterByPhoneEventDTO authOtpRegisterByPhoneEventDTO);

    void listenAuthOtpForgotPasswordByEmailEvent(
            AuthOtpForgotPasswordByEmailEventDTO authOtpForgotPasswordByEmailEventDTO);

    void listenAuthOtpForgotPasswordByEmailEventDlt(
            AuthOtpForgotPasswordByEmailEventDTO authOtpForgotPasswordByEmailEventDTO);

    void listenAuthOtpForgotPasswordByPhoneEvent(
            AuthOtpForgotPasswordByPhoneEventDTO authOtpForgotPasswordByPhoneEventDTO);

    void listenAuthOtpForgotPasswordByPhoneEventDlt(
            AuthOtpForgotPasswordByPhoneEventDTO authOtpForgotPasswordByPhoneEventDTO);

    void listenAuthOtpSecurityEvent(AuthOtpSecurityEventDTO event);

    void listenAuthOtpSecurityEventDlt(AuthOtpSecurityEventDTO event);

    void listenNotificationCreated(NotificationCreatedEventDTO event);

    void listenPreferencesUpdated(NotificationPreferencesUpdatedEventDTO event);
}
