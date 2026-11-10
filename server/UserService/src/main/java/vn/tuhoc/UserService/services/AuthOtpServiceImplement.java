package vn.tuhoc.UserService.services;

import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.entities.AuthOtpEntity;
import vn.tuhoc.UserService.domains.entities.UserEntity;
import vn.tuhoc.UserService.domains.entities.UsernameEntity;
import vn.tuhoc.UserService.domains.enums.AuthOtpPurposeEnum;
import vn.tuhoc.UserService.domains.mappers.AuthOtpMapper;
import vn.tuhoc.UserService.dtos.events.AuthOtpForgotPasswordByEmailEventDTO;
import vn.tuhoc.UserService.dtos.events.AuthOtpForgotPasswordByPhoneEventDTO;
import vn.tuhoc.UserService.dtos.events.AuthOtpRegisterByEmailEventDTO;
import vn.tuhoc.UserService.dtos.events.AuthOtpRegisterByPhoneEventDTO;
import vn.tuhoc.UserService.dtos.events.AuthOtpSecurityEventDTO;
import vn.tuhoc.UserService.dtos.requests.AuthOtpResetPasswordRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthOtpCreateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthOtpForgotPasswordRequestDTO;
import vn.tuhoc.UserService.dtos.requests.AuthOtpVerifyRequestDTO;
import vn.tuhoc.UserService.dtos.responses.AuthOtpDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.AuthOtpVerificationResponseDTO;
import vn.tuhoc.UserService.exceptions.AuthOtpAlreadyVerifiedException;
import vn.tuhoc.UserService.exceptions.AuthOtpExpiredException;
import vn.tuhoc.UserService.exceptions.AuthOtpInvalidCodeException;
import vn.tuhoc.UserService.exceptions.AuthMfaSettingException;
import vn.tuhoc.UserService.exceptions.AuthOtpIsNotVerifiedException;
import vn.tuhoc.UserService.exceptions.AuthOtpNotFoundByPublicIdException;
import vn.tuhoc.UserService.exceptions.UsernameNotFoundByTypeAndValueException;
import vn.tuhoc.UserService.repositories.AuthOtpRepository;
import vn.tuhoc.UserService.repositories.UserRepository;
import vn.tuhoc.UserService.repositories.UsernameRepository;
import vn.tuhoc.UserService.exceptions.UserNotFoundByIdException;
import vn.tuhoc.UserService.services.interfaces.AuthOtpEncryptionService;
import vn.tuhoc.UserService.services.interfaces.AuthOtpService;
import vn.tuhoc.UserService.services.interfaces.ProducerService;
import vn.tuhoc.UserService.utils.IdentifierUtil;
import vn.tuhoc.UserService.utils.PasswordUtil;
import vn.tuhoc.core.utils.TimeUtil;
import vn.tuhoc.core.utils.ValidationUtil;

@Service
@RequiredArgsConstructor
@Transactional(noRollbackFor = AuthOtpInvalidCodeException.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthOtpServiceImplement implements AuthOtpService {
    static final int MAX_FAILED_ATTEMPTS = 5;
    static final SecureRandom SECURE_RANDOM = new SecureRandom();
    ProducerService producerService;
    AuthOtpEncryptionService authOtpEncryptionService;
    UserRepository userRepository;
    UsernameRepository usernameRepository;
    AuthOtpRepository authOtpRepository;
    AuthOtpMapper authOtpMapper;

    private String generateCode() {
        return String.format(Locale.ROOT, "%06d", SECURE_RANDOM.nextInt(1_000_000));
    }

    private AuthOtpEntity getByPublicId(String publicId) {
        return this.authOtpRepository.findByPublicId(publicId)
                .orElseThrow(() -> new AuthOtpNotFoundByPublicIdException(publicId));
    }

    @Override
    public AuthOtpDetailResponseDTO handleGetByPublicId(String publicId) {
        AuthOtpEntity entity = this.getByPublicId(publicId);
        AuthOtpDetailResponseDTO response = this.authOtpMapper.entityToDetailResponse(entity);
        response.setCode(null);
        if (this.isSecurityPurpose(entity.getPurpose())) {
            response.getUsername().setValue(this.maskIdentifier(response.getUsername().getValue()));
        }
        return response;
    }

    @Override
    public AuthOtpDetailResponseDTO handleCreate(AuthOtpCreateRequestDTO authOtpCreateRequestDTO) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        String code = this.generateCode();

        AuthOtpEntity authOtpEntity = this.authOtpMapper.createEntityFromRequest(authOtpCreateRequestDTO);
        authOtpEntity.setPublicId(UUID.randomUUID().toString());
        authOtpEntity.setCreatedAt(TimeUtil.getDatetime(currentDateTime));
        authOtpEntity.setExpiredAt(TimeUtil.getDatetime(currentDateTime.plusMinutes(1).plusSeconds(30)));
        authOtpEntity.setFailedAttempts(0);
        authOtpEntity.setCode(this.authOtpEncryptionService.encrypt(code, authOtpEntity.getPublicId()));

        AuthOtpDetailResponseDTO response = this.authOtpMapper
                .entityToDetailResponse(this.authOtpRepository.save(authOtpEntity));
        response.setCode(code);
        return response;
    }

    @Override
    public AuthOtpDetailResponseDTO handleCreateSecurityOtp(Integer userId, AuthOtpPurposeEnum purpose) {
        if (purpose != AuthOtpPurposeEnum.LOGIN && purpose != AuthOtpPurposeEnum.ENABLE_MFA
                && purpose != AuthOtpPurposeEnum.DISABLE_MFA
                && purpose != AuthOtpPurposeEnum.CHANGE_PASSWORD) {
            throw new IllegalArgumentException("Security OTP purpose is invalid");
        }

        AuthOtpEntity pendingOtp = this.authOtpRepository
                .findFirstByUserIdAndPurposeAndVerifiedAtIsNullOrderByCreatedAtDesc(userId, purpose)
                .orElse(null);
        if (pendingOtp != null && TimeUtil.getLocalDateTime(TimeUtil.getCurrentDatetime())
                .isBefore(TimeUtil.getLocalDateTime(pendingOtp.getExpiredAt()))) {
            AuthOtpDetailResponseDTO existingChallenge = this.authOtpMapper.entityToDetailResponse(pendingOtp);
            existingChallenge.setCode(null);

            return existingChallenge;
        }

        UserEntity user = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));
        UsernameEntity destination = user.getUsernames().stream()
                .filter(username -> Boolean.TRUE.equals(username.getIsVerify()))
                .filter(username -> Boolean.TRUE.equals(username.getIsActive()))
                .filter(username -> username.getType() == vn.tuhoc.UserService.domains.enums.UsernameTypeEnum.EMAIL
                        || username.getType() == vn.tuhoc.UserService.domains.enums.UsernameTypeEnum.PHONE)
                .findFirst()
                .orElseThrow(() -> new AuthMfaSettingException(
                        "Cần có email hoặc số điện thoại đã xác minh để nhận mã OTP."));

        AuthOtpDetailResponseDTO otp = this.handleCreate(AuthOtpCreateRequestDTO.builder()
                .userId(userId)
                .usernameId(destination.getId())
                .purpose(purpose)
                .build());

        AuthOtpSecurityEventDTO event = AuthOtpSecurityEventDTO.builder()
                .userId(userId)
                .expiredAt(otp.getExpiredAt())
                .code(otp.getCode())
                .email(destination.getType() == vn.tuhoc.UserService.domains.enums.UsernameTypeEnum.EMAIL
                        ? destination.getValue() : null)
                .phone(destination.getType() == vn.tuhoc.UserService.domains.enums.UsernameTypeEnum.PHONE
                        ? destination.getValue() : null)
                .firstName(user.getProfile() == null ? null : user.getProfile().getFirstName())
                .lastName(user.getProfile() == null ? null : user.getProfile().getLastName())
                .purpose(purpose.getDescription())
                .build();
        this.producerService.sendAuthOtpSecurityEvent(event);

        return otp;
    }

    @Override
    public AuthOtpDetailResponseDTO handleForgotPassword(
            AuthOtpForgotPasswordRequestDTO authOtpForgotPasswordRequestDTO) {
        UsernameEntity usernameEntity = this.usernameRepository
                .findByTypeAndValue(authOtpForgotPasswordRequestDTO.getUsernameType(),
                        authOtpForgotPasswordRequestDTO.getUsernameValue())
                .orElseThrow(() -> new UsernameNotFoundByTypeAndValueException());
        UserEntity userEntity = usernameEntity.getUser();

        AuthOtpCreateRequestDTO authOtpCreateRequestDTO = AuthOtpCreateRequestDTO.builder()
                .userId(userEntity.getId())
                .usernameId(usernameEntity.getId())
                .purpose(AuthOtpPurposeEnum.RESET_PASSWORD)
                .build();
        AuthOtpDetailResponseDTO authOtpDetailResponseDTO = this.handleCreate(authOtpCreateRequestDTO);

        if (IdentifierUtil.isEmail(authOtpDetailResponseDTO.getUsername().getValue())) {
            AuthOtpForgotPasswordByEmailEventDTO authOtpForgotPasswordByEmailEventDTO = AuthOtpForgotPasswordByEmailEventDTO
                    .builder()
                    .userId(userEntity.getId())
                    .expiredAt(authOtpDetailResponseDTO.getExpiredAt())
                    .code(authOtpDetailResponseDTO.getCode())
                    .email(authOtpDetailResponseDTO.getUsername().getValue())
                    .firstName(userEntity.getProfile().getFirstName())
                    .lastName(userEntity.getProfile().getLastName())
                    .build();
            this.producerService.sendAuthOtpForgotPasswordByEmailEvent(authOtpForgotPasswordByEmailEventDTO);
        } else if (IdentifierUtil.isPhone(authOtpDetailResponseDTO.getUsername().getValue())) {
            AuthOtpForgotPasswordByPhoneEventDTO authOtpForgotPasswordByPhoneEventDTO = AuthOtpForgotPasswordByPhoneEventDTO
                    .builder()
                    .userId(userEntity.getId())
                    .expiredAt(authOtpDetailResponseDTO.getExpiredAt())
                    .code(authOtpDetailResponseDTO.getCode())
                    .phone(authOtpDetailResponseDTO.getUsername().getValue())
                    .firstName(userEntity.getProfile().getFirstName())
                    .lastName(userEntity.getProfile().getLastName())
                    .build();
            this.producerService.sendAuthOtpForgotPasswordByPhoneEvent(authOtpForgotPasswordByPhoneEventDTO);
        }

        return authOtpDetailResponseDTO;
    }

    @Override
    public void handleResetPassword(
            AuthOtpResetPasswordRequestDTO authOtpResetPasswordRequestDTO) {
        AuthOtpEntity authOtpEntityExists = this.getByPublicId(authOtpResetPasswordRequestDTO.getPublicId());
        if (authOtpEntityExists.getPurpose() != AuthOtpPurposeEnum.RESET_PASSWORD
                || ValidationUtil.isNull(authOtpEntityExists.getVerifiedAt())
                || authOtpEntityExists.getCode() == null) {
            throw new AuthOtpIsNotVerifiedException();
        }
//        if (!TimeUtil.getLocalDateTime(TimeUtil.getCurrentDatetime())
//                .isBefore(TimeUtil.getLocalDateTime(authOtpEntityExists.getExpiredAt()))) {
//            throw new AuthOtpExpiredException();
//        }

        UserEntity userEntity = authOtpEntityExists.getUser();

        String newPasswordRequest = authOtpResetPasswordRequestDTO.getNewPassword();
        String confirmPasswordRequest = authOtpResetPasswordRequestDTO.getConfirmPassword();
        PasswordUtil.handlePasswordIsNotMatch(newPasswordRequest, confirmPasswordRequest);

        String newPassword = PasswordUtil.getPasswordEncoder().encode(newPasswordRequest);
        userEntity.setPassword(newPassword);
        authOtpEntityExists.setCode(null);
    }

    @Override
    public AuthOtpDetailResponseDTO handleVerify(AuthOtpVerifyRequestDTO authOtpVerifyRequestDTO) {
        AuthOtpEntity authOtpEntity = this.getByPublicId(authOtpVerifyRequestDTO.getPublicId());
        if (this.isSecurityPurpose(authOtpEntity.getPurpose())) {
            throw new AuthOtpInvalidCodeException();
        }
        this.verifyEntity(authOtpEntity, authOtpVerifyRequestDTO.getCode());
        AuthOtpDetailResponseDTO response = this.authOtpMapper.entityToDetailResponse(authOtpEntity);
        response.setCode(null);
        return response;
    }

    @Override
    public AuthOtpVerificationResponseDTO handleVerifyForPurpose(
            AuthOtpVerifyRequestDTO authOtpVerifyRequestDTO,
            AuthOtpPurposeEnum expectedPurpose,
            Integer expectedUserId) {
        AuthOtpEntity authOtpEntity = this.getByPublicId(authOtpVerifyRequestDTO.getPublicId());
        if (authOtpEntity.getPurpose() != expectedPurpose
                || (expectedUserId != null && !expectedUserId.equals(authOtpEntity.getUser().getId()))) {
            throw new AuthOtpInvalidCodeException();
        }

        this.verifyEntity(authOtpEntity, authOtpVerifyRequestDTO.getCode());

        return AuthOtpVerificationResponseDTO.builder()
                .userId(authOtpEntity.getUser().getId())
                .username(authOtpEntity.getUsername().getValue())
                .purpose(authOtpEntity.getPurpose())
                .build();
    }

    private void verifyEntity(AuthOtpEntity authOtpEntity, String suppliedCode) {
        if (authOtpEntity.getVerifiedAt() != null) {
            throw new AuthOtpAlreadyVerifiedException();
        }
        if (!TimeUtil.getLocalDateTime(TimeUtil.getCurrentDatetime())
                .isBefore(TimeUtil.getLocalDateTime(authOtpEntity.getExpiredAt()))) {
            throw new AuthOtpExpiredException();
        }

        Integer failedAttempts = authOtpEntity.getFailedAttempts() == null ? 0 : authOtpEntity.getFailedAttempts();
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            throw new AuthOtpExpiredException();
        }

        String storedCode = this.authOtpEncryptionService.decrypt(authOtpEntity.getCode(), authOtpEntity.getPublicId());
        boolean matches = storedCode != null && suppliedCode != null
                && MessageDigest.isEqual(storedCode.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                        suppliedCode.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        if (!matches) {
            authOtpEntity.setFailedAttempts(failedAttempts + 1);
            this.authOtpRepository.save(authOtpEntity);
            throw new AuthOtpInvalidCodeException();
        }
        authOtpEntity.setVerifiedAt(TimeUtil.getCurrentDatetime());

        if (authOtpEntity.getPurpose() == AuthOtpPurposeEnum.REGISTER
                || authOtpEntity.getPurpose() == AuthOtpPurposeEnum.VERIFY_EMAIL
                || authOtpEntity.getPurpose() == AuthOtpPurposeEnum.VERIFY_PHONE) {
            authOtpEntity.getUsername().setIsVerify(true);
        }
    }

    @Override
    public AuthOtpDetailResponseDTO handleResend(String publicId) {
        AuthOtpEntity authOtpEntityExists = this.getByPublicId(publicId);
        if (this.isSecurityPurpose(authOtpEntityExists.getPurpose())) {
            throw new AuthOtpInvalidCodeException();
        }
        UserEntity userEntity = this.userRepository.findById(authOtpEntityExists.getUser().getId())
                .orElseThrow(() -> new UserNotFoundByIdException(authOtpEntityExists.getUser().getId()));
        authOtpEntityExists.setExpiredAt(TimeUtil.getCurrentDatetime());

        AuthOtpCreateRequestDTO authOtpCreateRequestDTO = AuthOtpCreateRequestDTO.builder()
                .userId(authOtpEntityExists.getUser().getId())
                .usernameId(authOtpEntityExists.getUsername().getId())
                .purpose(authOtpEntityExists.getPurpose())
                .build();
        AuthOtpDetailResponseDTO authOtpDetailResponseDTO = this.handleCreate(authOtpCreateRequestDTO);

        // Email
        AuthOtpPurposeEnum authOtpPurposeEnum = authOtpDetailResponseDTO.getPurpose();
        if (IdentifierUtil.isEmail(authOtpDetailResponseDTO.getUsername().getValue())) {
            if (authOtpPurposeEnum.equals(AuthOtpPurposeEnum.REGISTER)) {
                AuthOtpRegisterByEmailEventDTO authOtpRegisterByEmailEventDTO = AuthOtpRegisterByEmailEventDTO
                        .builder()
                        .userId(userEntity.getId())
                        .expiredAt(authOtpDetailResponseDTO.getExpiredAt())
                        .code(authOtpDetailResponseDTO.getCode())
                        .email(authOtpDetailResponseDTO.getUsername().getValue())
                        .firstName(userEntity.getProfile().getFirstName())
                        .lastName(userEntity.getProfile().getLastName())
                        .build();
                this.producerService.sendAuthOtpRegisterByEmailEvent(authOtpRegisterByEmailEventDTO);
            } else if (authOtpPurposeEnum.equals(AuthOtpPurposeEnum.RESET_PASSWORD)) {
                AuthOtpForgotPasswordByEmailEventDTO authOtpForgotPasswordByEmailEventDTO = AuthOtpForgotPasswordByEmailEventDTO
                        .builder()
                        .userId(userEntity.getId())
                        .expiredAt(authOtpDetailResponseDTO.getExpiredAt())
                        .code(authOtpDetailResponseDTO.getCode())
                        .email(authOtpDetailResponseDTO.getUsername().getValue())
                        .firstName(userEntity.getProfile().getFirstName())
                        .lastName(userEntity.getProfile().getLastName())
                        .build();
                this.producerService.sendAuthOtpForgotPasswordByEmailEvent(authOtpForgotPasswordByEmailEventDTO);
            }
        }
        // Phone
        if (IdentifierUtil.isPhone(authOtpDetailResponseDTO.getUsername().getValue())) {
            if (authOtpPurposeEnum.equals(AuthOtpPurposeEnum.REGISTER)) {
                AuthOtpRegisterByPhoneEventDTO authOtpRegisterByPhoneEventDTO = AuthOtpRegisterByPhoneEventDTO
                        .builder()
                        .userId(userEntity.getId())
                        .expiredAt(authOtpDetailResponseDTO.getExpiredAt())
                        .code(authOtpDetailResponseDTO.getCode())
                        .phone(authOtpDetailResponseDTO.getUsername().getValue())
                        .firstName(userEntity.getProfile().getFirstName())
                        .lastName(userEntity.getProfile().getLastName())
                        .build();
                this.producerService.sendAuthOtpRegisterByPhoneEvent(authOtpRegisterByPhoneEventDTO);
            } else if (authOtpPurposeEnum.equals(AuthOtpPurposeEnum.RESET_PASSWORD)) {
                AuthOtpForgotPasswordByPhoneEventDTO authOtpForgotPasswordByPhoneEventDTO = AuthOtpForgotPasswordByPhoneEventDTO
                        .builder()
                        .userId(userEntity.getId())
                        .expiredAt(authOtpDetailResponseDTO.getExpiredAt())
                        .code(authOtpDetailResponseDTO.getCode())
                        .phone(authOtpDetailResponseDTO.getUsername().getValue())
                        .firstName(userEntity.getProfile().getFirstName())
                        .lastName(userEntity.getProfile().getLastName())
                        .build();
                this.producerService.sendAuthOtpForgotPasswordByPhoneEvent(authOtpForgotPasswordByPhoneEventDTO);
            }
        }

        return authOtpDetailResponseDTO;
    }

    private boolean isSecurityPurpose(AuthOtpPurposeEnum purpose) {
        return purpose == AuthOtpPurposeEnum.LOGIN || purpose == AuthOtpPurposeEnum.ENABLE_MFA
                || purpose == AuthOtpPurposeEnum.CHANGE_PASSWORD
                || purpose == AuthOtpPurposeEnum.DISABLE_MFA;
    }

    private String maskIdentifier(String value) {
        if (value == null) {
            return "";
        }
        int at = value.indexOf('@');
        if (at > 0) {
            String local = value.substring(0, at);
            String domain = value.substring(at + 1);
            int dot = domain.lastIndexOf('.');
            String domainName = dot > 0 ? domain.substring(0, dot) : domain;
            String suffix = dot > 0 ? domain.substring(dot) : "";
            return local.substring(0, 1) + "***@" + domainName.substring(0, 1) + "***" + suffix;
        }
        String digits = value.replaceAll("\\D", "");
        return "***" + (digits.length() > 3 ? digits.substring(digits.length() - 3) : digits);
    }
}
