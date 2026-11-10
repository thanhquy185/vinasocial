package vn.tuhoc.RelationshipService.services;

import java.nio.charset.StandardCharsets;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.MGF1ParameterSpec;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import vn.tuhoc.RelationshipService.domains.entities.ConversationE2eeDeviceKeyEntity;
import vn.tuhoc.RelationshipService.dtos.requests.ConversationE2eeDeviceKeyRegisterRequestDTO;
import vn.tuhoc.RelationshipService.dtos.responses.ConversationE2eeDeviceKeyResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.ConversationE2eeDevicePublicKeyResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.ConversationE2eePairingCodeResponseDTO;
import vn.tuhoc.RelationshipService.exceptions.E2eeDeviceKeyConflictException;
import vn.tuhoc.RelationshipService.exceptions.E2eePairingCodeInvalidException;
import vn.tuhoc.RelationshipService.repositories.ConversationE2eeDeviceKeyRepository;
import vn.tuhoc.core.utils.TimeUtil;
import vn.tuhoc.security.utils.SecurityUtil;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationE2eeDeviceKeyService {
    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_PENDING_APPROVAL = "PENDING_APPROVAL";
    private static final String STATUS_CODE_ISSUED = "CODE_ISSUED";
    private static final String STATUS_REVOKED = "REVOKED";
    private static final int PAIRING_CODE_LENGTH = 8;
    private static final int MAX_PAIRING_FAILED_ATTEMPTS = 5;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String PAIRING_HMAC_CONTEXT = "vinasocial:e2ee-device-pairing:v1:";
    private static final String REBIND_HMAC_CONTEXT = "vinasocial:e2ee-device-rebind:v1:";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    ConversationE2eeDeviceKeyRepository deviceKeyRepository;

    @Value("${jwt.base64-secret}")
    @NonFinal
    String jwtBase64Secret;

    public ConversationE2eeDeviceKeyResponseDTO register(
            ConversationE2eeDeviceKeyRegisterRequestDTO request) {
        Integer currentUserId = this.getCurrentUserId();
        String authSessionDeviceId = this.getCurrentSessionDeviceId(
                "Phiên đăng nhập không hợp lệ để đăng ký thiết bị.");
        LocalDateTime now = LocalDateTime.now();
        ConversationE2eeDeviceKeyEntity deviceKey = this.deviceKeyRepository
                .findById(request.getDeviceId())
                .orElse(null);

        if (deviceKey == null) {
            boolean hasActiveDevice = !this.deviceKeyRepository
                    .findAllByUserIdAndStatusAndRevokedAtIsNull(currentUserId, STATUS_ACTIVE).isEmpty();
            deviceKey = ConversationE2eeDeviceKeyEntity.builder()
                    .deviceId(request.getDeviceId())
                    .userId(currentUserId)
                    .createdAt(TimeUtil.getDatetime(now))
                    .status(hasActiveDevice ? STATUS_PENDING_APPROVAL : STATUS_ACTIVE)
                    .authSessionDeviceId(authSessionDeviceId)
                    .pairingFailedAttempts(0)
                    .pairingExpiresAt(hasActiveDevice ? TimeUtil.getDatetime(now.plusMinutes(10)) : null)
                    .build();
        }

        if (!deviceKey.getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("Thiết bị mã hóa không thuộc tài khoản hiện tại.");
        }
        if (deviceKey.getRevokedAt() != null || STATUS_REVOKED.equals(deviceKey.getStatus())) {
            throw new AccessDeniedException("Khóa của thiết bị này đã bị thu hồi.");
        }
        if (deviceKey.getPublicKey() != null && !deviceKey.getPublicKey().equals(request.getPublicKey())) {
            throw new E2eeDeviceKeyConflictException(
                    "Thiết bị này đã đăng ký khóa khác. Hãy thu hồi khóa cũ rồi đăng ký thiết bị mới.");
        }
        if (deviceKey.getAuthSessionDeviceId() != null
                && !deviceKey.getAuthSessionDeviceId().equals(authSessionDeviceId)) {
            throw new E2eeDeviceKeyConflictException(
                    "Khóa mã hóa này đã được liên kết với một phiên đăng nhập khác. Hãy đăng ký thiết bị mới.");
        }

        deviceKey.setDeviceName(request.getDeviceName().trim());
        deviceKey.setPublicKey(request.getPublicKey());
        deviceKey.setAuthSessionDeviceId(authSessionDeviceId);
        deviceKey.setLastSeenAt(TimeUtil.getDatetime(now));
        boolean pairingExpired = deviceKey.getPairingExpiresAt() == null
                || !now.isBefore(TimeUtil.getLocalDateTime(deviceKey.getPairingExpiresAt()));
        if ((STATUS_PENDING_APPROVAL.equals(deviceKey.getStatus())
                || STATUS_CODE_ISSUED.equals(deviceKey.getStatus())) && pairingExpired) {
            deviceKey.setStatus(STATUS_PENDING_APPROVAL);
            deviceKey.setPairingCodeMac(null);
            deviceKey.setPairingExpiresAt(TimeUtil.getDatetime(now.plusMinutes(10)));
            deviceKey.setPairingFailedAttempts(0);
        }
        return this.toResponse(this.deviceKeyRepository.save(deviceKey));
    }

    public String startRebind(String deviceId) {
        Integer userId = this.getCurrentUserId();
        String sessionDeviceId = this.getCurrentSessionDeviceId("Phiên đăng nhập không hợp lệ để khôi phục thiết bị.");
        ConversationE2eeDeviceKeyEntity device = this.getOwnedDevice(deviceId, userId);
        if (device.getRevokedAt() != null || STATUS_REVOKED.equals(device.getStatus())) {
            throw new AccessDeniedException("Khóa thiết bị đã bị thu hồi.");
        }
        if (sessionDeviceId.equals(device.getAuthSessionDeviceId())) {
            throw new E2eeDeviceKeyConflictException("Thiết bị đã gắn với phiên hiện tại.");
        }
        byte[] challenge = new byte[32];
        SECURE_RANDOM.nextBytes(challenge);
        String answer = Base64.getEncoder().encodeToString(challenge);
        try {
            JsonNode jwk = OBJECT_MAPPER.readTree(device.getPublicKey());
            BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(jwk.path("n").asText()));
            BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(jwk.path("e").asText()));
            PublicKey publicKey = KeyFactory.getInstance("RSA")
                    .generatePublic(new RSAPublicKeySpec(modulus, exponent));
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey, new OAEPParameterSpec(
                    "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT));
            String encryptedChallenge = Base64.getEncoder().encodeToString(
                    cipher.doFinal(answer.getBytes(StandardCharsets.US_ASCII)));
            device.setRebindChallengeMac(this.createMac(
                    REBIND_HMAC_CONTEXT + userId + ":" + deviceId + ":" + sessionDeviceId + ":" + answer));
            device.setRebindSessionDeviceId(sessionDeviceId);
            device.setRebindExpiresAt(TimeUtil.getDatetime(LocalDateTime.now().plusMinutes(3)));
            this.deviceKeyRepository.save(device);
            return encryptedChallenge;
        } catch (Exception exception) {
            throw new E2eeDeviceKeyConflictException("Không thể tạo thử thách khôi phục thiết bị.");
        }
    }

    public ConversationE2eeDeviceKeyResponseDTO completeRebind(String deviceId, String answer) {
        Integer userId = this.getCurrentUserId();
        String sessionDeviceId = this.getCurrentSessionDeviceId("Phiên đăng nhập không hợp lệ để khôi phục thiết bị.");
        ConversationE2eeDeviceKeyEntity device = this.getOwnedDevice(deviceId, userId);
        boolean valid = device.getRevokedAt() == null
                && sessionDeviceId.equals(device.getRebindSessionDeviceId())
                && device.getRebindExpiresAt() != null
                && LocalDateTime.now().isBefore(TimeUtil.getLocalDateTime(device.getRebindExpiresAt()))
                && device.getRebindChallengeMac() != null
                && MessageDigest.isEqual(
                        device.getRebindChallengeMac().getBytes(StandardCharsets.US_ASCII),
                        this.createMac(REBIND_HMAC_CONTEXT + userId + ":" + deviceId + ":"
                                + sessionDeviceId + ":" + answer).getBytes(StandardCharsets.US_ASCII));
        if (!valid) {
            throw new E2eeDeviceKeyConflictException("Thử thách khôi phục thiết bị không hợp lệ hoặc đã hết hạn.");
        }
        device.setAuthSessionDeviceId(sessionDeviceId);
        device.setRebindChallengeMac(null);
        device.setRebindExpiresAt(null);
        device.setRebindSessionDeviceId(null);
        if (STATUS_CODE_ISSUED.equals(device.getStatus())) {
            device.setStatus(STATUS_PENDING_APPROVAL);
            device.setPairingCodeMac(null);
            device.setPairingExpiresAt(TimeUtil.getDatetime(LocalDateTime.now().plusMinutes(10)));
            device.setPairingFailedAttempts(0);
        }
        device.setLastSeenAt(TimeUtil.getCurrentDatetime());
        return this.toResponse(this.deviceKeyRepository.save(device));
    }

    public ConversationE2eePairingCodeResponseDTO approvePairing(String deviceId) {
        Integer currentUserId = this.getCurrentUserId();
        String authSessionDeviceId = this.getCurrentSessionDeviceId(
                "Phiên đăng nhập không hợp lệ để duyệt thiết bị.");
        ConversationE2eeDeviceKeyEntity approver = this.deviceKeyRepository
                .findFirstByUserIdAndAuthSessionDeviceIdAndStatusAndRevokedAtIsNull(
                        currentUserId, authSessionDeviceId, STATUS_ACTIVE)
                .orElseThrow(() -> new AccessDeniedException(
                        "Chỉ phiên đã liên kết với thiết bị E2EE đang hoạt động mới có thể duyệt thiết bị."));
        if (approver.getDeviceId().equals(deviceId)) {
            throw new AccessDeniedException("Thiết bị mới không thể tự duyệt chính nó.");
        }

        ConversationE2eeDeviceKeyEntity pending = this.getOwnedDevice(deviceId, currentUserId);
        if (!STATUS_PENDING_APPROVAL.equals(pending.getStatus()) || pending.getRevokedAt() != null) {
            throw new E2eeDeviceKeyConflictException("Yêu cầu thêm thiết bị không còn chờ duyệt.");
        }
        LocalDateTime now = LocalDateTime.now();
        if (pending.getPairingExpiresAt() == null
                || !now.isBefore(TimeUtil.getLocalDateTime(pending.getPairingExpiresAt()))) {
            throw new E2eeDeviceKeyConflictException(
                    "Yêu cầu ghép nối đã hết hạn. Tải lại thiết bị mới để tạo yêu cầu khác.");
        }

        boolean anotherCodeIsLive = this.deviceKeyRepository
                .findAllByUserIdAndStatusAndRevokedAtIsNull(currentUserId, STATUS_CODE_ISSUED).stream()
                .filter(device -> !device.getDeviceId().equals(deviceId))
                .anyMatch(device -> device.getPairingExpiresAt() != null
                        && now.isBefore(TimeUtil.getLocalDateTime(device.getPairingExpiresAt())));
        if (anotherCodeIsLive) {
            throw new E2eeDeviceKeyConflictException(
                    "Đang có mã ghép nối khác còn hiệu lực. Hoàn tất hoặc đợi mã đó hết hạn trước.");
        }

        String code = String.format(Locale.ROOT, "%0" + PAIRING_CODE_LENGTH + "d",
                SECURE_RANDOM.nextInt(100_000_000));
        LocalDateTime expiresAt = now.plusMinutes(3);
        pending.setStatus(STATUS_CODE_ISSUED);
        pending.setPairingCodeMac(this.createPairingCodeMac(currentUserId, deviceId, code));
        pending.setPairingExpiresAt(TimeUtil.getDatetime(expiresAt));
        pending.setPairingFailedAttempts(0);
        this.deviceKeyRepository.save(pending);
        return new ConversationE2eePairingCodeResponseDTO(deviceId, code, TimeUtil.getDatetime(expiresAt));
    }

    @Transactional(dontRollbackOn = E2eePairingCodeInvalidException.class)
    public ConversationE2eeDeviceKeyResponseDTO completePairing(String deviceId, String code) {
        Integer currentUserId = this.getCurrentUserId();
        String authSessionDeviceId = this.getCurrentSessionDeviceId(
                "Phiên đăng nhập không hợp lệ để hoàn tất ghép nối.");
        ConversationE2eeDeviceKeyEntity pending = this.getOwnedDevice(deviceId, currentUserId);
        if (!STATUS_CODE_ISSUED.equals(pending.getStatus())
                || !authSessionDeviceId.equals(pending.getAuthSessionDeviceId())
                || pending.getRevokedAt() != null) {
            throw new E2eePairingCodeInvalidException();
        }

        LocalDateTime now = LocalDateTime.now();
        boolean expired = pending.getPairingExpiresAt() == null
                || !now.isBefore(TimeUtil.getLocalDateTime(pending.getPairingExpiresAt()));
        boolean matches = !expired
                && pending.getPairingCodeMac() != null
                && MessageDigest.isEqual(
                        pending.getPairingCodeMac().getBytes(StandardCharsets.US_ASCII),
                        this.createPairingCodeMac(currentUserId, deviceId, code)
                                .getBytes(StandardCharsets.US_ASCII));
        if (!matches) {
            int failures = pending.getPairingFailedAttempts() == null ? 0 : pending.getPairingFailedAttempts();
            pending.setPairingFailedAttempts(failures + 1);
            if (expired || failures + 1 >= MAX_PAIRING_FAILED_ATTEMPTS) {
                pending.setStatus(STATUS_PENDING_APPROVAL);
                pending.setPairingCodeMac(null);
                pending.setPairingExpiresAt(TimeUtil.getDatetime(now.plusMinutes(10)));
                pending.setPairingFailedAttempts(0);
            }
            this.deviceKeyRepository.save(pending);
            throw new E2eePairingCodeInvalidException();
        }

        pending.setStatus(STATUS_ACTIVE);
        pending.setPairingCodeMac(null);
        pending.setPairingExpiresAt(null);
        pending.setPairingFailedAttempts(0);
        pending.setLastSeenAt(TimeUtil.getDatetime(now));
        return this.toResponse(this.deviceKeyRepository.save(pending));
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ConversationE2eeDeviceKeyResponseDTO> getMyDevices() {
        Integer currentUserId = this.getCurrentUserId();
        return this.deviceKeyRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ConversationE2eeDevicePublicKeyResponseDTO> getPublicKeys(Collection<Integer> userIds) {
        this.getCurrentUserId();
        return this.deviceKeyRepository.findAllByUserIdInAndRevokedAtIsNull(userIds).stream()
                .filter(deviceKey -> STATUS_ACTIVE.equals(deviceKey.getStatus()))
                .map(deviceKey -> new ConversationE2eeDevicePublicKeyResponseDTO(
                        deviceKey.getUserId(),
                        deviceKey.getDeviceId(),
                        deviceKey.getPublicKey()))
                .toList();
    }

    public int revoke(String deviceId) {
        Integer currentUserId = this.getCurrentUserId();
        ConversationE2eeDeviceKeyEntity deviceKey = this.deviceKeyRepository
                .findByDeviceIdAndUserId(deviceId, currentUserId)
                .orElseThrow(() -> new AccessDeniedException("Không tìm thấy khóa của thiết bị này."));
        if (deviceKey.getRevokedAt() == null) {
            deviceKey.setRevokedAt(TimeUtil.getCurrentDatetime());
            deviceKey.setStatus(STATUS_REVOKED);
            deviceKey.setPairingCodeMac(null);
            deviceKey.setPairingExpiresAt(null);
            this.deviceKeyRepository.save(deviceKey);
        }
        return 1;
    }

    private ConversationE2eeDeviceKeyResponseDTO toResponse(ConversationE2eeDeviceKeyEntity deviceKey) {
        return new ConversationE2eeDeviceKeyResponseDTO(
                deviceKey.getUserId(),
                deviceKey.getDeviceId(),
                deviceKey.getDeviceName(),
                deviceKey.getPublicKey(),
                deviceKey.getCreatedAt(),
                deviceKey.getLastSeenAt(),
                deviceKey.getRevokedAt(),
                deviceKey.getStatus(),
                deviceKey.getPairingExpiresAt());
    }

    private ConversationE2eeDeviceKeyEntity getOwnedDevice(String deviceId, Integer userId) {
        ConversationE2eeDeviceKeyEntity deviceKey = this.deviceKeyRepository.findById(deviceId)
                .orElseThrow(() -> new AccessDeniedException("Không tìm thấy thiết bị nhắn tin."));
        if (!deviceKey.getUserId().equals(userId)) {
            throw new AccessDeniedException("Thiết bị nhắn tin không thuộc tài khoản hiện tại.");
        }
        return deviceKey;
    }

    private String createPairingCodeMac(Integer userId, String deviceId, String code) {
        return this.createMac(PAIRING_HMAC_CONTEXT + userId + ":" + deviceId + ":" + code);
    }

    private String createMac(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            byte[] key = Base64.getDecoder().decode(this.jwtBase64Secret);
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            byte[] digest = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception exception) {
            throw new IllegalStateException("Không thể xác thực mã ghép nối thiết bị.", exception);
        }
    }

    private String getCurrentSessionDeviceId(String message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AccessDeniedException(message);
        }
        Map<String, Object> user = jwt.getClaim("user");
        Object sessionDeviceId = user == null ? null : user.get("sessionDeviceId");
        if (!(sessionDeviceId instanceof String value) || value.isBlank()) {
            throw new AccessDeniedException(message);
        }
        return value;
    }

    private Integer getCurrentUserId() {
        return SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("Bạn cần đăng nhập để quản lý khóa mã hóa."));
    }
}
