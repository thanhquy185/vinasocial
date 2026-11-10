package vn.tuhoc.RelationshipService.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.RelationshipService.dtos.requests.ConversationE2eeHistoryKeyWrapperItemRequestDTO;
import vn.tuhoc.RelationshipService.domains.entities.ConversationE2eeDeviceKeyEntity;
import vn.tuhoc.RelationshipService.domains.entities.ConversationEntity;
import vn.tuhoc.RelationshipService.domains.entities.ConversationMessageDeviceKeyWrapperEntity;
import vn.tuhoc.RelationshipService.domains.entities.MessageEntity;
import vn.tuhoc.RelationshipService.domains.enums.ConversationTypeEnum;
import vn.tuhoc.RelationshipService.exceptions.E2eeDeviceKeyConflictException;
import vn.tuhoc.RelationshipService.exceptions.MessageNotFoundException;
import vn.tuhoc.RelationshipService.repositories.BlockRepository;
import vn.tuhoc.RelationshipService.repositories.ConversationE2eeDeviceKeyRepository;
import vn.tuhoc.RelationshipService.repositories.ConversationMessageDeviceKeyWrapperRepository;
import vn.tuhoc.RelationshipService.repositories.ConversationParticipantRepository;
import vn.tuhoc.RelationshipService.repositories.ConversationRepository;
import vn.tuhoc.RelationshipService.repositories.ConversationRequestRepository;
import vn.tuhoc.RelationshipService.domains.enums.ConversationRequestStatusEnum;
import vn.tuhoc.RelationshipService.repositories.MessageRepository;
import vn.tuhoc.RelationshipService.utils.E2eeCiphertextFingerprintUtil;
import vn.tuhoc.RelationshipService.utils.E2eeEnvelopeValidationUtil;
import vn.tuhoc.core.utils.TimeUtil;
import vn.tuhoc.security.utils.SecurityUtil;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class E2eeMessageKeyWrapperService {
    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final int MAX_WRAPPERS_PER_BATCH = 50;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    ConversationE2eeDeviceKeyRepository deviceKeyRepository;
    ConversationMessageDeviceKeyWrapperRepository wrapperRepository;
    MessageRepository messageRepository;
    ConversationRepository conversationRepository;
    ConversationRequestRepository conversationRequestRepository;
    ConversationParticipantRepository participantRepository;
    BlockRepository blockRepository;

    public int addHistoryKeyWrappers(
            String targetDeviceId,
            List<ConversationE2eeHistoryKeyWrapperItemRequestDTO> items) {
        Integer userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("Bạn cần đăng nhập để chuyển lịch sử mã hóa."));
        if (items == null || items.isEmpty() || items.size() > MAX_WRAPPERS_PER_BATCH) {
            throw new E2eeDeviceKeyConflictException("Mỗi lần chỉ có thể chuyển từ 1 đến 50 khóa tin nhắn.");
        }

        String sessionDeviceId = this.getCurrentSessionDeviceId();
        ConversationE2eeDeviceKeyEntity sourceDevice = this.deviceKeyRepository
                .findFirstByUserIdAndAuthSessionDeviceIdAndStatusAndRevokedAtIsNull(
                        userId, sessionDeviceId, STATUS_ACTIVE)
                .orElseThrow(() -> new AccessDeniedException(
                        "Phiên hiện tại chưa được liên kết với một thiết bị E2EE đang hoạt động."));
        ConversationE2eeDeviceKeyEntity targetDevice = this.deviceKeyRepository.findById(targetDeviceId)
                .filter(device -> userId.equals(device.getUserId())
                        && STATUS_ACTIVE.equals(device.getStatus())
                        && device.getRevokedAt() == null)
                .orElseThrow(() -> new AccessDeniedException("Thiết bị nhận lịch sử không hoạt động hoặc không thuộc tài khoản."));
        if (sourceDevice.getDeviceId().equals(targetDevice.getDeviceId())) {
            throw new E2eeDeviceKeyConflictException("Không thể chuyển lịch sử cho chính thiết bị hiện tại.");
        }

        Set<Integer> seenMessageIds = new HashSet<>();
        for (var item : items) {
            if (!seenMessageIds.add(item.getMessageId())) {
                throw new E2eeDeviceKeyConflictException("Một lô chuyển không được lặp lại cùng tin nhắn.");
            }
        }

        Map<Integer, Boolean> conversationAccess = new HashMap<>();
        Map<Integer, Map<String, ConversationMessageDeviceKeyWrapperEntity>> existingWrappers = new HashMap<>();
        this.wrapperRepository.findAllByMessageIdInAndDeviceIdIn(
                        seenMessageIds, List.of(sourceDevice.getDeviceId(), targetDevice.getDeviceId()))
                .forEach(wrapper -> existingWrappers
                        .computeIfAbsent(wrapper.getMessageId(), ignored -> new HashMap<>())
                        .put(wrapper.getDeviceId(), wrapper));

        int changed = 0;
        List<ConversationMessageDeviceKeyWrapperEntity> wrappersToSave = new ArrayList<>();
        for (var item : items) {
            MessageEntity message = this.messageRepository.findById(item.getMessageId())
                    .orElseThrow(MessageNotFoundException::new);
            if (!conversationAccess.computeIfAbsent(message.getConversationId(),
                    conversationId -> this.canReadConversation(userId, conversationId))) {
                throw new AccessDeniedException("Bạn không có quyền chuyển khóa của tin nhắn này.");
            }
            if (!E2eeEnvelopeValidationUtil.isValid(message.getCiphertext())) {
                throw new E2eeDeviceKeyConflictException("Chỉ có thể chuyển khóa của tin nhắn E2EE hợp lệ.");
            }

            String ciphertextHash = E2eeCiphertextFingerprintUtil.sha256Hex(message.getCiphertext());
            if (!ciphertextHash.equals(item.getCiphertextHash())) {
                continue;
            }
            JsonNode envelope = this.readEnvelope(message.getCiphertext());
            JsonNode envelopeKeys = envelope.path("keys");
            if (envelopeKeys.has(targetDeviceId)) {
                continue;
            }
            Map<String, ConversationMessageDeviceKeyWrapperEntity> messageWrappers =
                    existingWrappers.getOrDefault(message.getId(), Map.of());
            ConversationMessageDeviceKeyWrapperEntity sourceWrapper = messageWrappers
                    .get(sourceDevice.getDeviceId());
            boolean sourceHasWrapper = envelopeKeys.has(sourceDevice.getDeviceId())
                    || (sourceWrapper != null && ciphertextHash.equals(sourceWrapper.getCiphertextHash()));
            if (!sourceHasWrapper) {
                throw new E2eeDeviceKeyConflictException(
                        "Thiết bị nguồn không có wrapper giải mã cho một hoặc nhiều tin nhắn trong lô.");
            }

            ConversationMessageDeviceKeyWrapperEntity wrapper = messageWrappers.get(targetDeviceId);
            if (wrapper == null) {
                wrapper = ConversationMessageDeviceKeyWrapperEntity.builder()
                        .messageId(message.getId())
                        .deviceId(targetDeviceId)
                        .build();
            }
            if (ciphertextHash.equals(wrapper.getCiphertextHash())) {
                continue;
            }
            wrapper.setWrappedKey(item.getWrappedKey());
            wrapper.setCiphertextHash(ciphertextHash);
            wrapper.setCreatedAt(TimeUtil.getCurrentDatetime());
            wrappersToSave.add(wrapper);
            changed++;
        }
        this.wrapperRepository.saveAll(wrappersToSave);
        return changed;
    }

    private boolean canReadConversation(Integer userId, Integer conversationId) {
        ConversationEntity conversation = this.conversationRepository.findById(conversationId)
                .orElseThrow(() -> new MessageNotFoundException());
        boolean activeParticipant = this.participantRepository
                .existsByConversationIdAndUserIdAndLeftAtIsNull(conversation.getId(), userId);
        boolean pendingRecipient = this.conversationRequestRepository.findByConversationId(conversationId)
                .filter(request -> request.getReceiverId().equals(userId)
                        && request.getStatus() == ConversationRequestStatusEnum.PENDING)
                .isPresent();
        if (!activeParticipant && !pendingRecipient) {
            return false;
        }
        if (conversation.getType() == ConversationTypeEnum.DIRECT) {
            return !this.blockRepository.existsByBlockerIdAndBlockedId(
                            conversation.getDirectUserLowId(), conversation.getDirectUserHighId())
                    && !this.blockRepository.existsByBlockerIdAndBlockedId(
                            conversation.getDirectUserHighId(), conversation.getDirectUserLowId());
        }
        return true;
    }

    private JsonNode readEnvelope(String ciphertext) {
        try {
            return OBJECT_MAPPER.readTree(ciphertext);
        } catch (Exception exception) {
            throw new E2eeDeviceKeyConflictException("Không đọc được envelope E2EE của tin nhắn.");
        }
    }

    private String getCurrentSessionDeviceId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AccessDeniedException("Phiên đăng nhập không hợp lệ để chuyển lịch sử.");
        }
        Map<String, Object> user = jwt.getClaim("user");
        Object sessionDeviceId = user == null ? null : user.get("sessionDeviceId");
        if (!(sessionDeviceId instanceof String value) || value.isBlank()) {
            throw new AccessDeniedException("Phiên đăng nhập không hợp lệ để chuyển lịch sử.");
        }
        return value;
    }
}
