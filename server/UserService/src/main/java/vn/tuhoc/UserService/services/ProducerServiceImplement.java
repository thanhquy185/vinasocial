package vn.tuhoc.UserService.services;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.dtos.events.AuthOtpForgotPasswordByEmailEventDTO;
import vn.tuhoc.UserService.dtos.events.AuthOtpForgotPasswordByPhoneEventDTO;
import vn.tuhoc.UserService.dtos.events.AuthOtpRegisterByEmailEventDTO;
import vn.tuhoc.UserService.dtos.events.AuthOtpRegisterByPhoneEventDTO;
import vn.tuhoc.UserService.dtos.events.AuthOtpSecurityEventDTO;
import vn.tuhoc.UserService.dtos.events.NotificationPreferencesUpdatedEventDTO;
import vn.tuhoc.UserService.dtos.events.UserCreatedEventDTO;
import vn.tuhoc.UserService.services.interfaces.ProducerService;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProducerServiceImplement implements ProducerService {
    KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendUserCreatedTopic(UserCreatedEventDTO userCreatedTopicDTO) {
        this.kafkaTemplate.send(
                "user_created",
                userCreatedTopicDTO.getUsername(),
                userCreatedTopicDTO);
    }

    @Override
    public void sendNotificationPreferencesUpdated(NotificationPreferencesUpdatedEventDTO event) {
        this.kafkaTemplate.send(
                "notification_preferences_updated",
                String.valueOf(event.getUserId()),
                event);
    }

    @Override
    public void sendAuthOtpRegisterByEmailEvent(AuthOtpRegisterByEmailEventDTO authOtpRegisterByEmailEventDTO) {
        this.kafkaTemplate.send(
                "auth_otp_register_by_email",
                String.valueOf(authOtpRegisterByEmailEventDTO.getUserId()),
                authOtpRegisterByEmailEventDTO);
    }

    @Override
    public void sendAuthOtpRegisterByPhoneEvent(AuthOtpRegisterByPhoneEventDTO authOtpRegisterByPhoneEventDTO) {
        this.kafkaTemplate.send(
                "auth_otp_register_by_phone",
                String.valueOf(authOtpRegisterByPhoneEventDTO.getUserId()),
                authOtpRegisterByPhoneEventDTO);
    }

    @Override
    public void sendAuthOtpForgotPasswordByEmailEvent(
            AuthOtpForgotPasswordByEmailEventDTO authOtpForgotPasswordByEmailEventDTO) {
        this.kafkaTemplate.send(
                "auth_otp_forgot_password_by_email",
                String.valueOf(authOtpForgotPasswordByEmailEventDTO.getUserId()),
                authOtpForgotPasswordByEmailEventDTO);
    }

    @Override
    public void sendAuthOtpForgotPasswordByPhoneEvent(
            AuthOtpForgotPasswordByPhoneEventDTO authOtpForgotPasswordByPhoneEventDTO) {
        this.kafkaTemplate.send(
                "auth_otp_forgot_password_by_phone",
                String.valueOf(authOtpForgotPasswordByPhoneEventDTO.getUserId()),
                authOtpForgotPasswordByPhoneEventDTO);
    }

    @Override
    public void sendAuthOtpSecurityEvent(AuthOtpSecurityEventDTO event) {
        this.kafkaTemplate.send("auth_otp_security", String.valueOf(event.getUserId()), event);
    }
}
