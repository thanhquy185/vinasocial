package vn.tuhoc.RelationshipService.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.RelationshipService.clients.UserClient;
import vn.tuhoc.RelationshipService.exceptions.ClientMessageIdReusedException;
import vn.tuhoc.RelationshipService.exceptions.ConversationNotFoundException;
import vn.tuhoc.RelationshipService.exceptions.ConversationRequestNotFoundException;
import vn.tuhoc.RelationshipService.exceptions.ConversationRequestNotPendingException;
import vn.tuhoc.RelationshipService.exceptions.DirectConversationMemberChangeNotAllowedException;
import vn.tuhoc.RelationshipService.exceptions.DuplicateGroupMemberException;
import vn.tuhoc.RelationshipService.exceptions.GroupMemberLimitReachedException;
import vn.tuhoc.RelationshipService.exceptions.InvalidClientMessageIdException;
import vn.tuhoc.RelationshipService.exceptions.InvalidGroupRoleException;
import vn.tuhoc.RelationshipService.exceptions.InvalidPageException;
import vn.tuhoc.RelationshipService.exceptions.MessageAlreadyDeletedException;
import vn.tuhoc.RelationshipService.exceptions.MessageNotFoundException;
import vn.tuhoc.RelationshipService.exceptions.MessageRequestPendingException;
import vn.tuhoc.RelationshipService.exceptions.OwnerRoleChangeNotAllowedException;
import vn.tuhoc.RelationshipService.exceptions.ReplyMessageDeletedException;
import vn.tuhoc.RelationshipService.exceptions.ReplyMessageNotFoundException;
import vn.tuhoc.RelationshipService.exceptions.SelfConversationNotAllowedException;
import vn.tuhoc.RelationshipService.exceptions.UserNotFoundException;
import vn.tuhoc.RelationshipService.exceptions.UserServiceUnavailableException;
import vn.tuhoc.RelationshipService.domains.entities.ConversationEntity;
import vn.tuhoc.RelationshipService.domains.entities.ConversationParticipantEntity;
import vn.tuhoc.RelationshipService.domains.entities.ConversationRequestEntity;
import vn.tuhoc.RelationshipService.domains.entities.ConversationMessageDeviceKeyWrapperEntity;
import vn.tuhoc.RelationshipService.domains.entities.MessageEntity;
import vn.tuhoc.RelationshipService.domains.enums.ConversationParticipantRoleEnum;
import vn.tuhoc.RelationshipService.domains.enums.ConversationRequestStatusEnum;
import vn.tuhoc.RelationshipService.domains.enums.ConversationTypeEnum;
import vn.tuhoc.RelationshipService.domains.enums.MessageStatusEnum;
import vn.tuhoc.RelationshipService.dtos.clients.UserProfileRelationshipResClientDTO;
import vn.tuhoc.RelationshipService.dtos.requests.GroupConversationCreateRequestDTO;
import vn.tuhoc.RelationshipService.dtos.requests.MessageCreateRequestDTO;
import vn.tuhoc.RelationshipService.dtos.requests.MessageUpdateRequestDTO;
import vn.tuhoc.RelationshipService.dtos.responses.ConversationParticipantSummaryResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.ConversationRequestSummaryResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.ConversationSummaryResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.DirectMessageResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.MessageDetailResponseDTO;
import vn.tuhoc.RelationshipService.repositories.BlockRepository;
import vn.tuhoc.RelationshipService.repositories.ConversationParticipantRepository;
import vn.tuhoc.RelationshipService.repositories.ConversationRepository;
import vn.tuhoc.RelationshipService.repositories.ConversationRequestRepository;
import vn.tuhoc.RelationshipService.repositories.ConversationE2eeDeviceKeyRepository;
import vn.tuhoc.RelationshipService.repositories.ConversationMessageDeviceKeyWrapperRepository;
import vn.tuhoc.RelationshipService.repositories.FollowRepository;
import vn.tuhoc.RelationshipService.repositories.MessageRepository;
import vn.tuhoc.RelationshipService.utils.E2eeCiphertextFingerprintUtil;
import vn.tuhoc.RelationshipService.services.interfaces.MessagingService;
import vn.tuhoc.RelationshipService.services.interfaces.ProducerService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.PageResponseUtil;
import vn.tuhoc.core.utils.TimeUtil;
import vn.tuhoc.security.utils.SecurityUtil;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessagingServiceImplement implements MessagingService {
    ConversationRepository conversationRepository;
    ConversationParticipantRepository participantRepository;
    ConversationRequestRepository conversationRequestRepository;
    MessageRepository messageRepository;
    ConversationE2eeDeviceKeyRepository deviceKeyRepository;
    ConversationMessageDeviceKeyWrapperRepository messageKeyWrapperRepository;
    FollowRepository followRepository;
    BlockRepository blockRepository;
    UserClient userClient;
    ProducerService producerService;
    MessageEncryptionSettingService encryptionSettingService;


    private MessageEntity storeMessage(
            ConversationEntity conversation, Integer senderId, MessageCreateRequestDTO request) {
        if (request.getReplyToMessageId() != null) {
            MessageEntity reply = this.messageRepository
                    .findByIdAndConversationId(request.getReplyToMessageId(), conversation.getId())
                    .orElseThrow(() -> new ReplyMessageNotFoundException());
            if (reply.getStatus() == MessageStatusEnum.DELETED) {
                throw new ReplyMessageDeletedException();
            }
        }
        String now = TimeUtil.getCurrentDatetime();
        MessageEntity message = this.messageRepository.save(MessageEntity.builder()
                .conversationId(conversation.getId())
                .senderId(senderId)
                .replyToMessageId(request.getReplyToMessageId())
                .clientMessageId(UUID.fromString(request.getClientMessageId()).toString())
                .createdAt(now)
                .type(request.getType())
                .ciphertext(request.getCiphertext())
                .status(MessageStatusEnum.SENT)
                .build());
        conversation.setLastMessageId(message.getId());
        conversation.setUpdatedAt(now);
        conversation.setLastActivityAt(now);
        this.conversationRepository.save(conversation);
        return message;
    }

    private void publishMessageNotifications(
            ConversationEntity conversation,
            Integer senderId,
            MessageEntity message) {
        Map<String, Object> metadata = Map.of(
                "conversationId", conversation.getId(),
                "messageId", message.getId(),
                "messageType", message.getType().name());

        if (conversation.getType() == ConversationTypeEnum.DIRECT) {
            Integer recipientId = conversation.getDirectUserLowId().equals(senderId)
                    ? conversation.getDirectUserHighId()
                    : conversation.getDirectUserLowId();
            this.producerService.publish(
                    recipientId,
                    senderId,
                    "SEND_MESSAGE",
                    "CONVERSATION",
                    conversation.getId(),
                    message.getId(),
                    "Tin nhắn mới",
                    "đã gửi cho bạn một tin nhắn.",
                    metadata);
            return;
        }

        this.participantRepository.findAllByConversationIdAndLeftAtIsNull(conversation.getId()).stream()
                .map(ConversationParticipantEntity::getUserId)
                .filter(userId -> !userId.equals(senderId))
                .distinct()
                .forEach(recipientId -> this.producerService.publish(
                        recipientId,
                        senderId,
                        "SEND_MESSAGE",
                        "CONVERSATION",
                        conversation.getId(),
                        message.getId(),
                        "Tin nhắn mới",
                        "đã gửi một tin nhắn trong nhóm.",
                        metadata));
    }

    private MessageEntity getIdempotentMessage(
            MessageEntity existing, MessageCreateRequestDTO request, Integer expectedConversationId) {
        if (expectedConversationId != null && !existing.getConversationId().equals(expectedConversationId)) {
            throw new ClientMessageIdReusedException("clientMessageId đã được dùng cho cuộc trò chuyện khác.");
        }
        if (!existing.getCiphertext().equals(request.getCiphertext()) || existing.getType() != request.getType()
                || !java.util.Objects.equals(existing.getReplyToMessageId(), request.getReplyToMessageId())) {
            throw new ClientMessageIdReusedException("clientMessageId đã được dùng cho nội dung khác.");
        }
        return existing;
    }

    private void acceptPendingRequest(ConversationRequestEntity request, Integer acceptingUserId) {
        if (request.getStatus() != ConversationRequestStatusEnum.PENDING
                || !request.getReceiverId().equals(acceptingUserId)) {
            throw new ConversationRequestNotPendingException();
        }
        this.addOrReactivateParticipant(request.getConversationId(), acceptingUserId,
                ConversationParticipantRoleEnum.MEMBER);
        request.setStatus(ConversationRequestStatusEnum.ACCEPTED);
        request.setRespondedAt(TimeUtil.getCurrentDatetime());
        this.conversationRequestRepository.save(request);
    }

    private void addOrReactivateParticipant(
            Integer conversationId, Integer userId, ConversationParticipantRoleEnum role) {
        Optional<ConversationParticipantEntity> existing = this.participantRepository
                .findByConversationIdAndUserId(conversationId, userId);
        if (existing.isPresent()) {
            ConversationParticipantEntity participant = existing.get();
            if (participant.getLeftAt() != null) {
                participant.setLeftAt(null);
                participant.setJoinedAt(TimeUtil.getCurrentDatetime());
            }
            if (role == ConversationParticipantRoleEnum.OWNER
                    || participant.getRole() == ConversationParticipantRoleEnum.OWNER) {
                participant.setRole(role);
            }
            this.participantRepository.save(participant);
            return;
        }
        this.participantRepository.save(ConversationParticipantEntity.builder()
                .conversationId(conversationId)
                .userId(userId)
                .role(role)
                .joinedAt(TimeUtil.getCurrentDatetime())
                .build());
    }

    private void transferGroupOwnership(Integer conversationId) {
        List<ConversationParticipantEntity> active = this.participantRepository
                .findAllByConversationIdAndLeftAtIsNullOrderByJoinedAtAsc(conversationId);
        Optional<ConversationParticipantEntity> nextOwner = active.stream()
                .filter(item -> item.getRole() == ConversationParticipantRoleEnum.ADMIN)
                .min(Comparator.comparing(ConversationParticipantEntity::getJoinedAt))
                .or(() -> active.stream().min(Comparator.comparing(ConversationParticipantEntity::getJoinedAt)));
        nextOwner.ifPresent(participant -> {
            participant.setRole(ConversationParticipantRoleEnum.OWNER);
            this.participantRepository.save(participant);
        });
    }

    private void ensureCanReadConversation(ConversationEntity conversation, Integer userId) {
        this.ensureConversationNotBlocked(conversation);
        if (this.participantRepository.existsByConversationIdAndUserIdAndLeftAtIsNull(conversation.getId(), userId)) {
            return;
        }
        boolean pendingRecipient = this.conversationRequestRepository.findByConversationId(conversation.getId())
                .filter(request -> request.getReceiverId().equals(userId)
                        && request.getStatus() == ConversationRequestStatusEnum.PENDING)
                .isPresent();
        if (!pendingRecipient) {
            throw new AccessDeniedException("Bạn không có quyền xem cuộc trò chuyện này.");
        }
    }

    private String getConversationState(Integer conversationId) {
        return this.conversationRequestRepository.findByConversationId(conversationId)
                .filter(request -> request.getStatus() == ConversationRequestStatusEnum.PENDING)
                .map(request -> "REQUESTED")
                .orElse("ACTIVE");
    }

    private void ensureConversationNotBlocked(ConversationEntity conversation) {
        if (conversation.getType() == ConversationTypeEnum.DIRECT) {
            this.ensureNotBlockedEitherWay(conversation.getDirectUserLowId(), conversation.getDirectUserHighId());
        }
    }

    private ConversationParticipantEntity requireActiveParticipant(Integer conversationId, Integer userId) {
        return this.participantRepository.findByConversationIdAndUserId(conversationId, userId)
                .filter(item -> item.getLeftAt() == null)
                .orElseThrow(() -> new AccessDeniedException("Bạn không phải thành viên đang hoạt động của cuộc trò chuyện này."));
    }

    private ConversationParticipantEntity requireAdmin(Integer conversationId, Integer userId) {
        ConversationParticipantEntity actor = this.requireActiveParticipant(conversationId, userId);
        if (actor.getRole() == ConversationParticipantRoleEnum.MEMBER) {
            throw new AccessDeniedException("Chỉ chủ nhóm hoặc quản trị viên mới có thể thực hiện thao tác này.");
        }
        return actor;
    }

    private ConversationEntity getConversation(Integer conversationId) {
        return this.conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException());
    }

    private ConversationRequestEntity getRequestForReceiver(Integer requestId, Integer receiverId) {
        return this.conversationRequestRepository.findByIdAndReceiverId(requestId, receiverId)
                .orElseThrow(() -> new ConversationRequestNotFoundException());
    }

    private UserProfileRelationshipResClientDTO getProfile(String username) {
        try {
            RestResponseDTO<UserProfileRelationshipResClientDTO> response = this.userClient.getPublicProfile(username);
            UserProfileRelationshipResClientDTO profile = response == null ? null : response.getData();
            if (profile == null || profile.getUserId() == null) {
                throw new UserServiceUnavailableException();
            }
            return profile;
        } catch (FeignException.NotFound exception) {
            throw new UserNotFoundException();
        } catch (UserServiceUnavailableException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new UserServiceUnavailableException();
        }
    }

    private Integer getCurrentUserId() {
        return SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("Bạn cần đăng nhập để thực hiện thao tác này."));
    }

    private boolean isMutualFollow(Integer firstUserId, Integer secondUserId) {
        return this.followRepository.existsByFollowerIdAndFollowingId(firstUserId, secondUserId)
                && this.followRepository.existsByFollowerIdAndFollowingId(secondUserId, firstUserId);
    }

    private void ensureNotBlockedEitherWay(Integer firstUserId, Integer secondUserId) {
        if (this.blockRepository.existsByBlockerIdAndBlockedId(firstUserId, secondUserId)
                || this.blockRepository.existsByBlockerIdAndBlockedId(secondUserId, firstUserId)) {
            throw new AccessDeniedException("Không thể nhắn tin do một trong hai tài khoản đã chặn tài khoản còn lại.");
        }
    }

    private void ensureNotSelf(Integer currentUserId, Integer targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new SelfConversationNotAllowedException();
        }
    }

    private UUID validateClientMessageId(String clientMessageId) {
        try {
            return UUID.fromString(clientMessageId);
        } catch (IllegalArgumentException exception) {
            throw new InvalidClientMessageIdException();
        }
    }

    private Pageable pageable(int page, int size, Sort sort) {
        if (page < 0 || size < 1 || size > 100) {
            throw new InvalidPageException();
        }
        return PageRequest.of(page, size, sort);
    }

    private ConversationSummaryResponseDTO toConversationSummary(ConversationEntity conversation) {
        return new ConversationSummaryResponseDTO(conversation.getId(), conversation.getType().name(),
                conversation.getName(), conversation.getLastMessageId(), conversation.getLastActivityAt(), null);
    }

    private ConversationSummaryResponseDTO toConversationSummary(
            ConversationEntity conversation, Integer lastReadMessageId) {
        return new ConversationSummaryResponseDTO(conversation.getId(), conversation.getType().name(),
                conversation.getName(), conversation.getLastMessageId(), conversation.getLastActivityAt(),
                lastReadMessageId);
    }

    private Map<Integer, ConversationParticipantEntity> getParticipantsByConversationId(
            List<ConversationEntity> conversations, Integer userId) {
        if (conversations.isEmpty()) {
            return Map.of();
        }
        List<Integer> conversationIds = conversations.stream().map(ConversationEntity::getId).toList();
        Map<Integer, ConversationParticipantEntity> participants = new HashMap<>();
        this.participantRepository.findAllByConversationIdInAndUserId(conversationIds, userId)
                .forEach(participant -> participants.put(participant.getConversationId(), participant));
        return participants;
    }

    private ConversationRequestSummaryResponseDTO toRequestSummary(ConversationRequestEntity request) {
        return new ConversationRequestSummaryResponseDTO(request.getId(), request.getConversationId(),
                request.getSenderId(), request.getReceiverId(), request.getCreatedAt(), request.getStatus().name());
    }

    private MessageDetailResponseDTO toMessageDetailResponse(MessageEntity message) {
        Integer currentUserId = this.getCurrentUserId();
        Map<Integer, Map<String, String>> wrappers = this.getDeviceKeyWrappers(List.of(message), currentUserId);
        return this.toMessageDetailResponse(message, wrappers.getOrDefault(message.getId(), Map.of()));
    }

    private MessageDetailResponseDTO toMessageDetailResponse(
            MessageEntity message, Map<String, String> deviceKeyWrappers) {
        return new MessageDetailResponseDTO(message.getId(), message.getConversationId(), message.getSenderId(),
                message.getReplyToMessageId(), message.getClientMessageId(), message.getCreatedAt(),
                message.getEditedAt(), message.getDeletedAt(), message.getType().name(), message.getCiphertext(),
                message.getStatus().name(), deviceKeyWrappers);
    }

    private Map<Integer, Map<String, String>> getDeviceKeyWrappers(List<MessageEntity> messages, Integer userId) {
        if (messages.isEmpty()) return Map.of();

        List<String> activeDeviceIds = this.deviceKeyRepository
                .findAllByUserIdAndStatusAndRevokedAtIsNull(userId, "ACTIVE").stream()
                .map(device -> device.getDeviceId())
                .toList();
        if (activeDeviceIds.isEmpty()) return Map.of();

        Map<Integer, MessageEntity> messagesById = new HashMap<>();
        List<Integer> messageIds = messages.stream().map(message -> {
            messagesById.put(message.getId(), message);
            return message.getId();
        }).toList();
        Map<Integer, Map<String, String>> wrappersByMessageId = new HashMap<>();
        List<ConversationMessageDeviceKeyWrapperEntity> wrappers = this.messageKeyWrapperRepository
                .findAllByMessageIdInAndDeviceIdIn(messageIds, activeDeviceIds);
        for (ConversationMessageDeviceKeyWrapperEntity wrapper : wrappers) {
            MessageEntity message = messagesById.get(wrapper.getMessageId());
            if (message == null || message.getCiphertext() == null || message.getCiphertext().isBlank()) {
                continue;
            }
            String currentCiphertextHash = E2eeCiphertextFingerprintUtil.sha256Hex(message.getCiphertext());
            if (!currentCiphertextHash.equals(wrapper.getCiphertextHash())) {
                continue;
            }
            wrappersByMessageId.computeIfAbsent(wrapper.getMessageId(), ignored -> new HashMap<>())
                    .put(wrapper.getDeviceId(), wrapper.getWrappedKey());
        }
        return wrappersByMessageId;
    }

    private DirectMessageResponseDTO toDirectMessageResponse(MessageEntity message, String conversationState) {
        return new DirectMessageResponseDTO(this.toMessageDetailResponse(message), conversationState);
    }

    private MessageEntity getMessage(Integer conversationId, Integer messageId) {
        return this.messageRepository.findByIdAndConversationId(messageId, conversationId)
                .orElseThrow(() -> new MessageNotFoundException());
    }

    private void ensureMessageOwner(MessageEntity message, Integer currentUserId) {
        if (!message.getSenderId().equals(currentUserId)) {
            throw new AccessDeniedException("Chỉ người gửi mới có thể sửa hoặc xóa tin nhắn này.");
        }
    }

    @Override
    public PageResponseDTO<ConversationSummaryResponseDTO> getConversations(int page, int size) {
        Integer currentUserId = this.getCurrentUserId();
        Pageable pageable = this.pageable(page, size, Sort.unsorted());
        Page<ConversationEntity> conversations = this.conversationRepository
                .findAllActiveByParticipantUserId(
                        currentUserId, ConversationRequestStatusEnum.ACCEPTED, pageable);
        Map<Integer, ConversationParticipantEntity> participants = this.getParticipantsByConversationId(
                conversations.getContent(), currentUserId);
        return PageResponseUtil.convert(conversations.map(conversation -> this.toConversationSummary(
                conversation,
                Optional.ofNullable(participants.get(conversation.getId()))
                        .map(ConversationParticipantEntity::getLastReadMessageId)
                        .orElse(null))));
    }

    @Override
    public int markConversationAsRead(Integer conversationId) {
        Integer currentUserId = this.getCurrentUserId();
        ConversationEntity conversation = this.getConversation(conversationId);
        this.ensureCanReadConversation(conversation, currentUserId);
        ConversationParticipantEntity participant = this.requireActiveParticipant(conversationId, currentUserId);
        participant.setLastReadMessageId(conversation.getLastMessageId());
        this.participantRepository.save(participant);
        return 1;
    }

    @Override
    public int markAllConversationsAsRead() {
        Integer currentUserId = this.getCurrentUserId();
        int pageIndex = 0;
        int updatedCount = 0;
        Page<ConversationEntity> conversations;
        do {
            conversations = this.conversationRepository.findAllActiveByParticipantUserId(
                    currentUserId,
                    ConversationRequestStatusEnum.ACCEPTED,
                    PageRequest.of(pageIndex, 100));
            Map<Integer, ConversationParticipantEntity> participants = this.getParticipantsByConversationId(
                    conversations.getContent(), currentUserId);
            List<ConversationParticipantEntity> changedParticipants = new ArrayList<>();
            for (ConversationEntity conversation : conversations.getContent()) {
                ConversationParticipantEntity participant = participants.get(conversation.getId());
                if (participant != null && !Objects.equals(
                        participant.getLastReadMessageId(), conversation.getLastMessageId())) {
                    participant.setLastReadMessageId(conversation.getLastMessageId());
                    changedParticipants.add(participant);
                }
            }
            if (!changedParticipants.isEmpty()) {
                this.participantRepository.saveAll(changedParticipants);
                updatedCount += changedParticipants.size();
            }
            pageIndex++;
        } while (!conversations.isLast());
        return updatedCount;
    }

    @Override
    public PageResponseDTO<ConversationRequestSummaryResponseDTO> getIncomingRequests(int page, int size) {
        Integer currentUserId = this.getCurrentUserId();
        Page<ConversationRequestEntity> requests = this.conversationRequestRepository
                .findAllByReceiverIdAndStatus(currentUserId, ConversationRequestStatusEnum.PENDING,
                        this.pageable(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id")));
        return PageResponseUtil.convert(requests.map(this::toRequestSummary));
    }

    @Override
    public PageResponseDTO<ConversationRequestSummaryResponseDTO> getOutgoingRequests(int page, int size) {
        Integer currentUserId = this.getCurrentUserId();
        Page<ConversationRequestEntity> requests = this.conversationRequestRepository
                .findAllBySenderIdAndStatus(currentUserId, ConversationRequestStatusEnum.PENDING,
                        this.pageable(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id")));
        return PageResponseUtil.convert(requests.map(this::toRequestSummary));
    }

    @Override
    public ConversationRequestSummaryResponseDTO acceptRequest(Integer requestId) {
        Integer currentUserId = this.getCurrentUserId();
        ConversationRequestEntity request = this.getRequestForReceiver(requestId, currentUserId);
        this.ensureNotBlockedEitherWay(request.getSenderId(), request.getReceiverId());
        if (request.getStatus() != ConversationRequestStatusEnum.PENDING) {
            throw new ConversationRequestNotPendingException();
        }

        this.addOrReactivateParticipant(request.getConversationId(), currentUserId,
                ConversationParticipantRoleEnum.MEMBER);
        request.setStatus(ConversationRequestStatusEnum.ACCEPTED);
        request.setRespondedAt(TimeUtil.getCurrentDatetime());
        this.conversationRequestRepository.save(request);
        return this.toRequestSummary(request);
    }

    @Override
    public ConversationRequestSummaryResponseDTO rejectRequest(Integer requestId) {
        Integer currentUserId = this.getCurrentUserId();
        ConversationRequestEntity request = this.getRequestForReceiver(requestId, currentUserId);
        if (request.getStatus() != ConversationRequestStatusEnum.PENDING) {
            throw new ConversationRequestNotPendingException();
        }
        request.setStatus(ConversationRequestStatusEnum.REJECTED);
        request.setRespondedAt(TimeUtil.getCurrentDatetime());
        this.conversationRequestRepository.save(request);
        return this.toRequestSummary(request);
    }

    @Override
    public ConversationRequestSummaryResponseDTO cancelRequest(Integer requestId) {
        Integer currentUserId = this.getCurrentUserId();
        ConversationRequestEntity request = this.conversationRequestRepository.findByIdAndSenderId(requestId,
                        currentUserId)
                .orElseThrow(() -> new ConversationRequestNotFoundException());
        if (request.getStatus() != ConversationRequestStatusEnum.PENDING) {
            throw new ConversationRequestNotPendingException();
        }
        request.setStatus(ConversationRequestStatusEnum.CANCELLED);
        request.setRespondedAt(TimeUtil.getCurrentDatetime());
        this.conversationRequestRepository.save(request);
        return this.toRequestSummary(request);
    }

    @Override
    public List<ConversationParticipantSummaryResponseDTO> getParticipants(Integer conversationId) {
        this.requireActiveParticipant(conversationId, this.getCurrentUserId());
        return this.participantRepository.findAllByConversationIdAndLeftAtIsNullOrderByJoinedAtAsc(conversationId)
                .stream()
                .map(item -> new ConversationParticipantSummaryResponseDTO(
                        item.getUserId(), item.getRole().name(), item.getJoinedAt()))
                .toList();
    }

    @Override
    public ConversationSummaryResponseDTO createGroup(GroupConversationCreateRequestDTO request) {
        Integer currentUserId = this.getCurrentUserId();
        Set<String> usernames = new HashSet<>();
        List<Integer> memberIds = new ArrayList<>();
        for (String username : request.getUsernames()) {
            String normalizedUsername = username.trim().toLowerCase(Locale.ROOT);
            if (!usernames.add(normalizedUsername)) {
                throw new DuplicateGroupMemberException();
            }
            Integer memberId = this.getProfile(username).getUserId();
            this.ensureNotSelf(currentUserId, memberId);
            memberIds.add(memberId);
        }

        String now = TimeUtil.getCurrentDatetime();
        ConversationEntity conversation = this.conversationRepository.save(ConversationEntity.builder()
                .createdById(currentUserId)
                .name(request.getName() == null || request.getName().isBlank() ? null : request.getName().trim())
                .createdAt(now)
                .updatedAt(now)
                .lastActivityAt(now)
                .type(ConversationTypeEnum.GROUP)
                .build());
        this.addOrReactivateParticipant(conversation.getId(), currentUserId, ConversationParticipantRoleEnum.OWNER);
        for (Integer memberId : memberIds) {
            this.addOrReactivateParticipant(conversation.getId(), memberId, ConversationParticipantRoleEnum.MEMBER);
        }
        return this.toConversationSummary(conversation);
    }

    @Override
    public ConversationSummaryResponseDTO addParticipant(Integer conversationId, String username) {
        Integer currentUserId = this.getCurrentUserId();
        ConversationEntity conversation = this.getConversation(conversationId);
        if (conversation.getType() != ConversationTypeEnum.GROUP) {
            throw new DirectConversationMemberChangeNotAllowedException("Không thể thêm thành viên vào cuộc trò chuyện cá nhân.");
        }
        this.requireAdmin(conversationId, currentUserId);

        Integer targetUserId = this.getProfile(username).getUserId();
        this.ensureNotSelf(currentUserId, targetUserId);
        this.ensureNotBlockedEitherWay(currentUserId, targetUserId);
        boolean alreadyActive = this.participantRepository.existsByConversationIdAndUserIdAndLeftAtIsNull(
                conversationId, targetUserId);
        if (!alreadyActive && this.participantRepository
                .findAllByConversationIdAndLeftAtIsNull(conversationId).size() >= 50) {
            throw new GroupMemberLimitReachedException();
        }
        this.addOrReactivateParticipant(conversationId, targetUserId, ConversationParticipantRoleEnum.MEMBER);
        return this.toConversationSummary(conversation);
    }

    @Override
    public ConversationSummaryResponseDTO updateParticipantRole(
            Integer conversationId, String username, ConversationParticipantRoleEnum role) {
        Integer currentUserId = this.getCurrentUserId();
        ConversationEntity conversation = this.getConversation(conversationId);
        if (conversation.getType() != ConversationTypeEnum.GROUP) {
            throw new DirectConversationMemberChangeNotAllowedException("Không thể đổi vai trò trong cuộc trò chuyện cá nhân.");
        }
        ConversationParticipantEntity owner = this.requireActiveParticipant(conversationId, currentUserId);
        if (owner.getRole() != ConversationParticipantRoleEnum.OWNER) {
            throw new AccessDeniedException("Chỉ chủ nhóm mới có thể thay đổi vai trò thành viên.");
        }
        if (role == null || role == ConversationParticipantRoleEnum.OWNER) {
            throw new InvalidGroupRoleException();
        }
        Integer targetUserId = this.getProfile(username).getUserId();
        if (currentUserId.equals(targetUserId)) {
            throw new OwnerRoleChangeNotAllowedException();
        }
        ConversationParticipantEntity target = this.requireActiveParticipant(conversationId, targetUserId);
        target.setRole(role);
        this.participantRepository.save(target);
        return this.toConversationSummary(conversation);
    }

    @Override
    public ConversationSummaryResponseDTO leaveConversation(Integer conversationId) {
        Integer currentUserId = this.getCurrentUserId();
        ConversationEntity conversation = this.getConversation(conversationId);
        ConversationParticipantEntity participant = this.requireActiveParticipant(conversationId, currentUserId);
        participant.setLeftAt(TimeUtil.getCurrentDatetime());
        this.participantRepository.save(participant);

        if (conversation.getType() == ConversationTypeEnum.GROUP
                && participant.getRole() == ConversationParticipantRoleEnum.OWNER) {
            this.transferGroupOwnership(conversationId);
        }
        return this.toConversationSummary(conversation);
    }

    @Override
    public DirectMessageResponseDTO sendDirectMessage(String username, MessageCreateRequestDTO request) {
        Integer currentUserId = this.getCurrentUserId();
        Integer targetUserId = this.getProfile(username).getUserId();
        this.ensureNotSelf(currentUserId, targetUserId);
        this.ensureNotBlockedEitherWay(currentUserId, targetUserId);
        UUID clientMessageId = this.validateClientMessageId(request.getClientMessageId());
        int lowId = Math.min(currentUserId, targetUserId);
        int highId = Math.max(currentUserId, targetUserId);
        this.conversationRepository.ensureDirectConversation(
                currentUserId, lowId, highId, TimeUtil.getCurrentDatetime());
        ConversationEntity conversation = this.conversationRepository.findDirectConversationForUpdate(
                ConversationTypeEnum.DIRECT, lowId, highId)
                .orElseThrow(() -> new ConversationNotFoundException());
        Optional<MessageEntity> existingMessage = this.messageRepository.findBySenderIdAndClientMessageId(
                currentUserId, clientMessageId.toString());
        if (existingMessage.isPresent()) {
            if (!existingMessage.get().getConversationId().equals(conversation.getId())) {
                throw new ClientMessageIdReusedException("clientMessageId đã được dùng cho cuộc trò chuyện khác.");
            }
            MessageEntity message = this.getIdempotentMessage(
                    existingMessage.get(), request, conversation.getId());
            return this.toDirectMessageResponse(message, this.getConversationState(conversation.getId()));
        }
        this.encryptionSettingService.validateOutgoing(currentUserId, request.getCiphertext());
        Optional<ConversationRequestEntity> existingRequest = this.conversationRequestRepository
                .findByConversationId(conversation.getId());
        String conversationState;

        if (existingRequest.isPresent()
                && existingRequest.get().getStatus() == ConversationRequestStatusEnum.PENDING) {
            ConversationRequestEntity pending = existingRequest.get();
            if (pending.getReceiverId().equals(currentUserId)) {
                this.acceptPendingRequest(pending, currentUserId);
                conversationState = "ACTIVE";
            } else {
                throw new MessageRequestPendingException("Yêu cầu nhắn tin của bạn đang chờ người nhận phản hồi.");
            }
        } else if (existingRequest.isPresent()
                && existingRequest.get().getStatus() == ConversationRequestStatusEnum.ACCEPTED) {
            this.addOrReactivateParticipant(conversation.getId(), currentUserId,
                    ConversationParticipantRoleEnum.MEMBER);
            this.addOrReactivateParticipant(conversation.getId(), targetUserId,
                    ConversationParticipantRoleEnum.MEMBER);
            conversationState = "ACTIVE";
        } else if (this.participantRepository.existsByConversationIdAndUserIdAndLeftAtIsNull(
                conversation.getId(), currentUserId)
                && this.participantRepository.existsByConversationIdAndUserIdAndLeftAtIsNull(
                        conversation.getId(), targetUserId)) {
            existingRequest.filter(item -> item.getStatus() == ConversationRequestStatusEnum.PENDING)
                    .ifPresent(item -> {
                        item.setStatus(ConversationRequestStatusEnum.ACCEPTED);
                        item.setRespondedAt(TimeUtil.getCurrentDatetime());
                        this.conversationRequestRepository.save(item);
                    });
            conversationState = "ACTIVE";
        } else if (this.isMutualFollow(currentUserId, targetUserId)) {
            this.addOrReactivateParticipant(conversation.getId(), currentUserId,
                    ConversationParticipantRoleEnum.MEMBER);
            this.addOrReactivateParticipant(conversation.getId(), targetUserId,
                    ConversationParticipantRoleEnum.MEMBER);
            existingRequest.filter(item -> item.getStatus() == ConversationRequestStatusEnum.PENDING)
                    .ifPresent(item -> {
                        item.setStatus(ConversationRequestStatusEnum.ACCEPTED);
                        item.setRespondedAt(TimeUtil.getCurrentDatetime());
                        this.conversationRequestRepository.save(item);
                    });
            conversationState = "ACTIVE";
        } else {
            this.addOrReactivateParticipant(conversation.getId(), currentUserId,
                    ConversationParticipantRoleEnum.MEMBER);
            ConversationRequestEntity messageRequest = existingRequest.orElseGet(() ->
                    ConversationRequestEntity.builder()
                            .conversationId(conversation.getId())
                            .createdAt(TimeUtil.getCurrentDatetime())
                            .build());
            messageRequest.setSenderId(currentUserId);
            messageRequest.setReceiverId(targetUserId);
            messageRequest.setCreatedAt(TimeUtil.getCurrentDatetime());
            messageRequest.setRespondedAt(null);
            messageRequest.setStatus(ConversationRequestStatusEnum.PENDING);
            this.conversationRequestRepository.save(messageRequest);
            conversationState = "REQUESTED";
        }

        MessageEntity message = this.storeMessage(conversation, currentUserId, request);
        this.publishMessageNotifications(conversation, currentUserId, message);
        return this.toDirectMessageResponse(message, conversationState);
    }

    @Override
    public MessageDetailResponseDTO sendConversationMessage(Integer conversationId, MessageCreateRequestDTO request) {
        Integer currentUserId = this.getCurrentUserId();
        ConversationEntity conversation = this.conversationRepository.findByIdForUpdate(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException());
        this.requireActiveParticipant(conversationId, currentUserId);
        this.ensureConversationNotBlocked(conversation);
        UUID clientMessageId = this.validateClientMessageId(request.getClientMessageId());
        Optional<MessageEntity> existingMessage = this.messageRepository.findBySenderIdAndClientMessageId(
                currentUserId, clientMessageId.toString());
        if (existingMessage.isPresent()) {
            return this.toMessageDetailResponse(
                    this.getIdempotentMessage(existingMessage.get(), request, conversationId));
        }
        this.encryptionSettingService.validateOutgoing(currentUserId, request.getCiphertext());

        if (conversation.getType() == ConversationTypeEnum.DIRECT) {
            Optional<ConversationRequestEntity> requestEntity = this.conversationRequestRepository
                    .findByConversationId(conversationId)
                    .filter(item -> item.getStatus() == ConversationRequestStatusEnum.PENDING);
            if (requestEntity.isPresent()) {
                ConversationRequestEntity pending = requestEntity.get();
                if (pending.getSenderId().equals(currentUserId)) {
                    throw new MessageRequestPendingException("Bạn chỉ có thể gửi thêm tin nhắn sau khi người nhận chấp nhận yêu cầu.");
                }
                this.acceptPendingRequest(pending, currentUserId);
            }
        }

        MessageEntity message = this.storeMessage(conversation, currentUserId, request);
        this.publishMessageNotifications(conversation, currentUserId, message);
        return this.toMessageDetailResponse(message);
    }

    @Override
    public PageResponseDTO<MessageDetailResponseDTO> getMessages(Integer conversationId, Integer beforeId, int size) {
        Integer currentUserId = this.getCurrentUserId();
        ConversationEntity conversation = this.getConversation(conversationId);
        this.ensureCanReadConversation(conversation, currentUserId);
        Pageable pageable = this.pageable(0, size, Sort.unsorted());
        Page<MessageEntity> messages = beforeId == null
                ? this.messageRepository.findAllByConversationIdOrderByIdDesc(conversationId, pageable)
                : this.messageRepository.findAllByConversationIdAndIdLessThanOrderByIdDesc(
                        conversationId, beforeId, pageable);
        Map<Integer, Map<String, String>> wrappers = this.getDeviceKeyWrappers(messages.getContent(), currentUserId);
        return PageResponseUtil.convert(messages.map(message -> this.toMessageDetailResponse(
                message, wrappers.getOrDefault(message.getId(), Map.of()))));
    }

    @Override
    public MessageDetailResponseDTO editMessage(
            Integer conversationId, Integer messageId, MessageUpdateRequestDTO request) {
        Integer currentUserId = this.getCurrentUserId();
        ConversationEntity conversation = this.getConversation(conversationId);
        this.requireActiveParticipant(conversationId, currentUserId);
        this.ensureConversationNotBlocked(conversation);
        MessageEntity message = this.getMessage(conversationId, messageId);
        this.ensureMessageOwner(message, currentUserId);
        if (message.getStatus() == MessageStatusEnum.DELETED) {
            throw new MessageAlreadyDeletedException();
        }
        this.encryptionSettingService.validateOutgoing(currentUserId, request.getCiphertext());
        message.setCiphertext(request.getCiphertext());
        message.setStatus(MessageStatusEnum.EDITED);
        message.setEditedAt(TimeUtil.getCurrentDatetime());
        this.messageRepository.save(message);
        return this.toMessageDetailResponse(message);
    }

    @Override
    public MessageDetailResponseDTO deleteMessage(Integer conversationId, Integer messageId) {
        Integer currentUserId = this.getCurrentUserId();
        ConversationEntity conversation = this.getConversation(conversationId);
        this.requireActiveParticipant(conversationId, currentUserId);
        this.ensureConversationNotBlocked(conversation);
        MessageEntity message = this.getMessage(conversationId, messageId);
        this.ensureMessageOwner(message, currentUserId);
        if (message.getStatus() != MessageStatusEnum.DELETED) {
            String now = TimeUtil.getCurrentDatetime();
            message.setCiphertext("");
            message.setStatus(MessageStatusEnum.DELETED);
            message.setDeletedAt(now);
            this.messageRepository.save(message);
            if (message.getId().equals(conversation.getLastMessageId())) {
                conversation.setUpdatedAt(now);
                conversation.setLastActivityAt(now);
                this.conversationRepository.save(conversation);
            }
        }
        return this.toMessageDetailResponse(message);
    }

}
