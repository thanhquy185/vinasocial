package vn.tuhoc.UserService.services.interfaces;

import vn.tuhoc.UserService.dtos.events.AuthOtpForgotPasswordByEmailEventDTO;
import vn.tuhoc.UserService.dtos.events.AuthOtpForgotPasswordByPhoneEventDTO;
import vn.tuhoc.UserService.dtos.events.AuthOtpRegisterByEmailEventDTO;
import vn.tuhoc.UserService.dtos.events.AuthOtpRegisterByPhoneEventDTO;
import vn.tuhoc.UserService.dtos.events.AuthOtpSecurityEventDTO;
import vn.tuhoc.UserService.dtos.events.NotificationPreferencesUpdatedEventDTO;
import vn.tuhoc.UserService.dtos.events.UserCreatedEventDTO;

public interface ProducerService {
    void sendUserCreatedTopic(UserCreatedEventDTO userCreatedTopicDTO);

    void sendNotificationPreferencesUpdated(NotificationPreferencesUpdatedEventDTO event);

    void sendAuthOtpRegisterByEmailEvent(AuthOtpRegisterByEmailEventDTO authOtpRegisterByEmailEventDTO);

    void sendAuthOtpRegisterByPhoneEvent(AuthOtpRegisterByPhoneEventDTO authOtpRegisterByPhoneEventDTO);

    void sendAuthOtpForgotPasswordByEmailEvent(
            AuthOtpForgotPasswordByEmailEventDTO authOtpForgotPasswordByEmailEventDTO);

    void sendAuthOtpForgotPasswordByPhoneEvent(
            AuthOtpForgotPasswordByPhoneEventDTO authOtpForgotPasswordByPhoneEventDTO);

    void sendAuthOtpSecurityEvent(AuthOtpSecurityEventDTO event);
}
