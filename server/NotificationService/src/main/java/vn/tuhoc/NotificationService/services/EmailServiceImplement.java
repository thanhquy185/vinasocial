package vn.tuhoc.NotificationService.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpForgotPasswordByEmailEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpRegisterByEmailEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpSecurityEventDTO;
import vn.tuhoc.NotificationService.exceptions.EmailOTPCantNotSendException;
import vn.tuhoc.NotificationService.services.interfaces.EmailService;
import vn.tuhoc.NotificationService.services.interfaces.EmailTemplateService;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailServiceImplement implements EmailService {
    @Value("${spring.mail.username}")
    @NonFinal
    String from;
    JavaMailSender javaMailSender;
    EmailTemplateService emailTemplateService;

    @Override
    public void sendRegisterByEmailOtp(AuthOtpRegisterByEmailEventDTO authOtpRegisterByEmailEventDTO) {
        MimeMessage message = this.javaMailSender.createMimeMessage();
        String html = this.emailTemplateService.renderRegisterByEmailOtp(authOtpRegisterByEmailEventDTO);

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(authOtpRegisterByEmailEventDTO.getEmail());
            helper.setSubject("VinaSocial - Xác thực tài khoản");
            helper.setText(html, true);

            this.javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailOTPCantNotSendException();
        }

    }

    @Override
    public void sendForgotPasswordByEmailOtp(
            AuthOtpForgotPasswordByEmailEventDTO authOtpForgotPasswordByEmailEventDTO) {
        MimeMessage message = this.javaMailSender.createMimeMessage();
        String html = this.emailTemplateService.renderForgotPasswordByEmailOtp(authOtpForgotPasswordByEmailEventDTO);

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(authOtpForgotPasswordByEmailEventDTO.getEmail());
            helper.setSubject("VinaSocial - Xác thực tài khoản");
            helper.setText(html, true);

            this.javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailOTPCantNotSendException();
        }
    }

    @Override
    public void sendSecurityOtpByEmail(AuthOtpSecurityEventDTO event) {
        MimeMessage message = this.javaMailSender.createMimeMessage();
        String html = this.emailTemplateService.renderSecurityOtp(event);
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(event.getEmail());
            helper.setSubject("VinaSocial - Mã xác thực bảo mật");
            helper.setText(html, true);
            this.javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailOTPCantNotSendException();
        }
    }
}
