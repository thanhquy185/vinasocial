package vn.tuhoc.NotificationService.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpForgotPasswordByPhoneEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpRegisterByPhoneEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpSecurityEventDTO;
import vn.tuhoc.NotificationService.exceptions.SmsOTPCantNotSendException;
import vn.tuhoc.NotificationService.services.interfaces.SmsService;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SmsServiceImplement implements SmsService {
    @Value("${twilio.account-sid}")
    String accountSid;
    @Value("${twilio.auth-token}")
    String authToken;
    @Value("${twilio.phone-from}")
    String phoneFrom;

    @Override
    public void sendRegisterByPhoneOtp(AuthOtpRegisterByPhoneEventDTO authOtpRegisterByPhoneEventDTO) {
        Twilio.init(accountSid, authToken);

        String message = String.format(
                "VinaSocial: Xin chào %s %s, mã xác thực để đăng ký tài khoản của bạn là %s. "
                        + "Mã có hiệu lực đến %s. Vui lòng không chia sẻ mã này với bất kỳ ai.",
                authOtpRegisterByPhoneEventDTO.getFirstName(),
                authOtpRegisterByPhoneEventDTO.getLastName(),
                authOtpRegisterByPhoneEventDTO.getCode(),
                authOtpRegisterByPhoneEventDTO.getExpiredAt());

        try {
            Message messageResponse = Message.creator(
                    new PhoneNumber(
                            authOtpRegisterByPhoneEventDTO.getPhone()),
                    new PhoneNumber(phoneFrom),
                    message).create();

            System.out.println("SMS SENT!");
            System.out.println("SID: " + messageResponse.getSid());
            System.out.println("STATUS: " + messageResponse.getStatus());

        } catch (ApiException e) {
            System.err.println("Twilio error:");
            System.err.println("Code: " + e.getCode());
            System.err.println("Status: " + e.getStatusCode());
            System.err.println("Message: " + e.getMessage());

            throw new SmsOTPCantNotSendException();
        }
    }

    @Override
    public void sendForgotPasswordByPhoneOtp(
            AuthOtpForgotPasswordByPhoneEventDTO authOtpForgotPasswordByPhoneEventDTO) {
        Twilio.init(accountSid, authToken);

        String message = String.format(
                "VinaSocial: Xin chào %s %s, mã xác thực để đặt lại mật khẩu là %s. "
                        + "Mã có hiệu lực đến %s. Vui lòng không chia sẻ mã này với bất kỳ ai.",
                authOtpForgotPasswordByPhoneEventDTO.getFirstName(),
                authOtpForgotPasswordByPhoneEventDTO.getLastName(),
                authOtpForgotPasswordByPhoneEventDTO.getCode(),
                authOtpForgotPasswordByPhoneEventDTO.getExpiredAt());

        try {
            Message.creator(
                    new PhoneNumber(authOtpForgotPasswordByPhoneEventDTO.getPhone()),
                    new PhoneNumber(phoneFrom),
                    message).create();
        } catch (ApiException e) {
            throw new SmsOTPCantNotSendException();
        }
    }

    @Override
    public void sendSecurityOtpByPhone(AuthOtpSecurityEventDTO event) {
        Twilio.init(accountSid, authToken);
        String message = String.format(
                "VinaSocial: Mã OTP để %s là %s. Mã có hiệu lực đến %s. Vui lòng không chia sẻ mã này.",
                event.getPurpose(), event.getCode(), event.getExpiredAt());
        try {
            Message.creator(new PhoneNumber(event.getPhone()), new PhoneNumber(phoneFrom), message).create();
        } catch (ApiException e) {
            throw new SmsOTPCantNotSendException();
        }
    }

}
