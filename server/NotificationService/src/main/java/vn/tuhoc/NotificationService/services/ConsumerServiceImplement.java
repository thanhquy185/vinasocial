package vn.tuhoc.NotificationService.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpForgotPasswordByEmailEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpForgotPasswordByPhoneEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpRegisterByEmailEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpRegisterByPhoneEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpSecurityEventDTO;
import vn.tuhoc.NotificationService.dtos.events.NotificationCreatedEventDTO;
import vn.tuhoc.NotificationService.dtos.events.NotificationPreferencesUpdatedEventDTO;
import vn.tuhoc.NotificationService.services.interfaces.ConsumerService;
import vn.tuhoc.NotificationService.services.interfaces.EmailService;
import vn.tuhoc.NotificationService.services.interfaces.NotificationService;
import vn.tuhoc.NotificationService.services.interfaces.SmsService;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConsumerServiceImplement implements ConsumerService {
    EmailService emailService;
    SmsService smsService;
    NotificationService notificationService;

    @Override
    @KafkaListener(topics = "auth_otp_register_by_email", containerFactory = "authOtpRegisterByEmailTopicListenerFactory")
    public void listenAuthOtpRegisterByEmailEvent(AuthOtpRegisterByEmailEventDTO authOtpRegisterByEmailEventDTO) {
        this.emailService.sendRegisterByEmailOtp(authOtpRegisterByEmailEventDTO);
    }

    @Override
    @KafkaListener(topics = "auth_otp_register_by_email-dlt", containerFactory = "authOtpRegisterByEmailTopicListenerFactory")
    public void listenAuthOtpRegisterByEmailEventDlt(AuthOtpRegisterByEmailEventDTO authOtpRegisterByEmailEventDTO) {
        this.emailService.sendRegisterByEmailOtp(authOtpRegisterByEmailEventDTO);
    }

    @Override
    @KafkaListener(topics = "auth_otp_register_by_phone", containerFactory = "authOtpRegisterByPhoneTopicListenerFactory")
    public void listenAuthOtpRegisterByPhoneEvent(AuthOtpRegisterByPhoneEventDTO authOtpRegisterByPhoneEventDTO) {
        this.smsService.sendRegisterByPhoneOtp(authOtpRegisterByPhoneEventDTO);
    }

    @Override
    @KafkaListener(topics = "auth_otp_register_by_phone-dlt", containerFactory = "authOtpRegisterByPhoneTopicListenerFactory")
    public void listenAuthOtpRegisterByPhoneEventDlt(AuthOtpRegisterByPhoneEventDTO authOtpRegisterByPhoneEventDTO) {
        this.smsService.sendRegisterByPhoneOtp(authOtpRegisterByPhoneEventDTO);
    }

    @Override
    @KafkaListener(topics = "auth_otp_forgot_password_by_email", containerFactory = "authOtpForgotPasswordByEmailTopicListenerFactory")
    public void listenAuthOtpForgotPasswordByEmailEvent(
            AuthOtpForgotPasswordByEmailEventDTO authOtpForgotPasswordByEmailEventDTO) {
        this.emailService.sendForgotPasswordByEmailOtp(authOtpForgotPasswordByEmailEventDTO);
    }

    @Override
    @KafkaListener(topics = "auth_otp_forgot_password_by_email-dlt", containerFactory = "authOtpForgotPasswordByEmailTopicListenerFactory")
    public void listenAuthOtpForgotPasswordByEmailEventDlt(
            AuthOtpForgotPasswordByEmailEventDTO authOtpForgotPasswordByEmailEventDTO) {
        this.emailService.sendForgotPasswordByEmailOtp(authOtpForgotPasswordByEmailEventDTO);
    }

    @Override
    @KafkaListener(topics = "auth_otp_forgot_password_by_phone", containerFactory = "authOtpForgotPasswordByPhoneTopicListenerFactory")
    public void listenAuthOtpForgotPasswordByPhoneEvent(
            AuthOtpForgotPasswordByPhoneEventDTO authOtpForgotPasswordByPhoneEventDTO) {
        this.smsService.sendForgotPasswordByPhoneOtp(authOtpForgotPasswordByPhoneEventDTO);
    }

    @Override
    @KafkaListener(topics = "auth_otp_forgot_password_by_phone-dlt", containerFactory = "authOtpForgotPasswordByPhoneTopicListenerFactory")
    public void listenAuthOtpForgotPasswordByPhoneEventDlt(
            AuthOtpForgotPasswordByPhoneEventDTO authOtpForgotPasswordByPhoneEventDTO) {
        this.smsService.sendForgotPasswordByPhoneOtp(authOtpForgotPasswordByPhoneEventDTO);
    }

    @Override
    @KafkaListener(topics = "auth_otp_security", containerFactory = "authOtpSecurityTopicListenerFactory")
    public void listenAuthOtpSecurityEvent(AuthOtpSecurityEventDTO event) {
        if (event.getEmail() != null && !event.getEmail().isBlank()) {
            this.emailService.sendSecurityOtpByEmail(event);
        } else if (event.getPhone() != null && !event.getPhone().isBlank()) {
            this.smsService.sendSecurityOtpByPhone(event);
        }
    }

    @Override
    @KafkaListener(topics = "auth_otp_security-dlt", containerFactory = "authOtpSecurityTopicListenerFactory")
    public void listenAuthOtpSecurityEventDlt(AuthOtpSecurityEventDTO event) {
        if (event.getEmail() != null && !event.getEmail().isBlank()) {
            this.emailService.sendSecurityOtpByEmail(event);
        } else if (event.getPhone() != null && !event.getPhone().isBlank()) {
            this.smsService.sendSecurityOtpByPhone(event);
        }
    }

    @Override
    @KafkaListener(topics = "notification_created", containerFactory = "notificationCreatedTopicListenerFactory")
    public void listenNotificationCreated(NotificationCreatedEventDTO event) {
        this.notificationService.handleCreatedEvent(event);
    }

    @Override
    @KafkaListener(
            topics = "notification_preferences_updated",
            containerFactory = "notificationPreferencesUpdatedTopicListenerFactory")
    public void listenPreferencesUpdated(NotificationPreferencesUpdatedEventDTO event) {
        this.notificationService.handlePreferencesUpdatedEvent(event);
    }
}
