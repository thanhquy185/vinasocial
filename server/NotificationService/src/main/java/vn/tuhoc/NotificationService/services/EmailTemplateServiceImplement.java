package vn.tuhoc.NotificationService.services;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpForgotPasswordByEmailEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpRegisterByEmailEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpSecurityEventDTO;
import vn.tuhoc.NotificationService.services.interfaces.EmailTemplateService;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailTemplateServiceImplement implements EmailTemplateService {
    SpringTemplateEngine springTemplateEngine;

    @Override
    public String renderRegisterByEmailOtp(AuthOtpRegisterByEmailEventDTO authOtpRegisterByEmailEventDTO) {
        Context context = new Context();
        context.setVariable("firstName", authOtpRegisterByEmailEventDTO.getFirstName());
        context.setVariable("lastName", authOtpRegisterByEmailEventDTO.getLastName());
        context.setVariable("email", authOtpRegisterByEmailEventDTO.getEmail());
        context.setVariable("expiredAt", authOtpRegisterByEmailEventDTO.getExpiredAt());
        context.setVariable("code", authOtpRegisterByEmailEventDTO.getCode());

        return this.springTemplateEngine.process("/register-otp", context);
    }

    @Override
    public String renderForgotPasswordByEmailOtp(
            AuthOtpForgotPasswordByEmailEventDTO authOtpForgotPasswordByEmailEventDTO) {
        Context context = new Context();
        context.setVariable("firstName", authOtpForgotPasswordByEmailEventDTO.getFirstName());
        context.setVariable("lastName", authOtpForgotPasswordByEmailEventDTO.getLastName());
        context.setVariable("email", authOtpForgotPasswordByEmailEventDTO.getEmail());
        context.setVariable("expiredAt", authOtpForgotPasswordByEmailEventDTO.getExpiredAt());
        context.setVariable("code", authOtpForgotPasswordByEmailEventDTO.getCode());

        return this.springTemplateEngine.process("/forgot-password-otp", context);
    }

    @Override
    public String renderSecurityOtp(AuthOtpSecurityEventDTO event) {
        Context context = new Context();
        context.setVariable("firstName", event.getFirstName());
        context.setVariable("lastName", event.getLastName());
        context.setVariable("email", event.getEmail());
        context.setVariable("expiredAt", event.getExpiredAt());
        context.setVariable("code", event.getCode());
        context.setVariable("purpose", event.getPurpose());
        return this.springTemplateEngine.process("/security-otp", context);
    }
}
