package vn.tuhoc.RelationshipService.services;

import java.util.Optional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import vn.tuhoc.RelationshipService.exceptions.FollowRequestNotFoundException;
import vn.tuhoc.RelationshipService.exceptions.FollowRequestNotPendingException;
import vn.tuhoc.RelationshipService.exceptions.InvalidPageException;
import vn.tuhoc.RelationshipService.exceptions.SelfRelationshipNotAllowedException;
import vn.tuhoc.RelationshipService.exceptions.UserNotFoundException;
import vn.tuhoc.RelationshipService.exceptions.UserServiceUnavailableException;
import vn.tuhoc.RelationshipService.domains.entities.BlockEntity;
import vn.tuhoc.RelationshipService.domains.entities.CloseFriendEntity;
import vn.tuhoc.RelationshipService.domains.entities.ConversationRequestEntity;
import vn.tuhoc.RelationshipService.domains.entities.FollowEntity;
import vn.tuhoc.RelationshipService.domains.entities.FollowRequestEntity;
import vn.tuhoc.RelationshipService.domains.enums.FollowRequestStatusEnum;
import vn.tuhoc.RelationshipService.domains.enums.ConversationRequestStatusEnum;
import vn.tuhoc.RelationshipService.dtos.clients.UserProfileRelationshipResClientDTO;
import vn.tuhoc.RelationshipService.dtos.responses.BlockSummaryResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.CloseFriendSummaryResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.FollowRequestSummaryResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.FollowSummaryResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.RelationshipActionResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.RelationshipStatusResponseDTO;
import vn.tuhoc.RelationshipService.repositories.BlockRepository;
import vn.tuhoc.RelationshipService.repositories.CloseFriendRepository;
import vn.tuhoc.RelationshipService.repositories.ConversationRequestRepository;
import vn.tuhoc.RelationshipService.repositories.FollowRepository;
import vn.tuhoc.RelationshipService.repositories.FollowRequestRepository;
import vn.tuhoc.RelationshipService.services.interfaces.RelationshipService;
import vn.tuhoc.RelationshipService.services.interfaces.ProducerService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.PageResponseUtil;
import vn.tuhoc.core.utils.TimeUtil;
import vn.tuhoc.core.utils.ValidationUtil;
import vn.tuhoc.security.utils.SecurityUtil;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RelationshipServiceImplement implements RelationshipService {
    FollowRepository followRepository;
    FollowRequestRepository followRequestRepository;
    BlockRepository blockRepository;
    CloseFriendRepository closeFriendRepository;
    ConversationRequestRepository conversationRequestRepository;
    ProducerService producerService;
    UserClient userClient;

    private UserProfileRelationshipResClientDTO getProfile(String username) {
        try {
            RestResponseDTO<UserProfileRelationshipResClientDTO> response = this.userClient.getPublicProfile(username);
            UserProfileRelationshipResClientDTO profile = ValidationUtil.isNull(response) ? null : response.getData();
            if (ValidationUtil.isNull(profile)
                    || ValidationUtil.isNull(profile.getUserId())
                    || ValidationUtil.isNull(profile.getIsPrivate())) {
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

    private FollowRequestEntity getIncomingRequest(Integer requestId, Integer receiverId) {
        return this.followRequestRepository.findByIdAndReceiverId(requestId, receiverId)
                .orElseThrow(() -> new FollowRequestNotFoundException());
    }

    private void createFollowIfMissing(Integer followerId, Integer followingId) {
        if (!this.followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            this.followRepository.save(FollowEntity.builder()
                    .followerId(followerId)
                    .followingId(followingId)
                    .createdAt(TimeUtil.getCurrentDatetime())
                    .build());
        }
    }

    private void cancelPendingRequest(Integer senderId, Integer receiverId) {
        this.followRequestRepository.findBySenderIdAndReceiverId(senderId, receiverId)
                .filter(request -> request.getStatus() == FollowRequestStatusEnum.PENDING)
                .ifPresent(request -> {
                    request.setStatus(FollowRequestStatusEnum.CANCELLED);
                    request.setRespondedAt(TimeUtil.getCurrentDatetime());
                    this.followRequestRepository.save(request);
                });
    }

    private void cancelPendingConversationRequest(Integer senderId, Integer receiverId) {
        this.conversationRequestRepository.findAllBySenderIdAndReceiverIdAndStatus(
                        senderId, receiverId, ConversationRequestStatusEnum.PENDING)
                .forEach(request -> {
                    request.setStatus(ConversationRequestStatusEnum.CANCELLED);
                    request.setRespondedAt(TimeUtil.getCurrentDatetime());
                    this.conversationRequestRepository.save(request);
                });
    }

    private void ensureNotBlockedEitherWay(Integer firstUserId, Integer secondUserId) {
        if (this.blockRepository.existsByBlockerIdAndBlockedId(firstUserId, secondUserId)
                || this.blockRepository.existsByBlockerIdAndBlockedId(secondUserId, firstUserId)) {
            throw new AccessDeniedException("Không thể thực hiện thao tác do một trong hai tài khoản đã chặn tài khoản còn lại.");
        }
    }

    private void ensureNotSelf(Integer currentUserId, Integer targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new SelfRelationshipNotAllowedException();
        }
    }

    private Pageable pageable(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new InvalidPageException();
        }
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
    }

    private FollowRequestSummaryResponseDTO toFollowRequestSummary(FollowRequestEntity request) {
        return new FollowRequestSummaryResponseDTO(request.getId(), request.getSenderId(), request.getReceiverId(),
                request.getCreatedAt(), request.getStatus().name());
    }

    public RelationshipActionResponseDTO follow(String username) {
        Integer currentUserId = this.getCurrentUserId();
        UserProfileRelationshipResClientDTO target = this.getProfile(username);
        Integer targetUserId = target.getUserId();
        this.ensureNotSelf(currentUserId, targetUserId);
        this.ensureNotBlockedEitherWay(currentUserId, targetUserId);

        if (this.followRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUserId)) {
            return new RelationshipActionResponseDTO(targetUserId, "ALREADY_FOLLOWING", true, false);
        }

        Optional<FollowRequestEntity> existingRequest = this.followRequestRepository
                .findBySenderIdAndReceiverId(currentUserId, targetUserId);
        if (Boolean.TRUE.equals(target.getIsPrivate())) {
            boolean shouldNotify = existingRequest.isEmpty()
                    || existingRequest.get().getStatus() != FollowRequestStatusEnum.PENDING;
            FollowRequestEntity request = existingRequest.orElseGet(() -> FollowRequestEntity.builder()
                    .senderId(currentUserId)
                    .receiverId(targetUserId)
                    .createdAt(TimeUtil.getCurrentDatetime())
                    .status(FollowRequestStatusEnum.PENDING)
                    .build());
            if (request.getStatus() != FollowRequestStatusEnum.PENDING) {
                request.setCreatedAt(TimeUtil.getCurrentDatetime());
                request.setRespondedAt(null);
                request.setStatus(FollowRequestStatusEnum.PENDING);
            }
            request = this.followRequestRepository.save(request);
            if (shouldNotify) {
                this.producerService.publish(
                        targetUserId,
                        currentUserId,
                        "FOLLOW_REQUEST",
                        "FOLLOW_REQUEST",
                        targetUserId,
                        request.getId(),
                        "Yêu cầu theo dõi mới",
                        "đã gửi cho bạn yêu cầu theo dõi.",
                        java.util.Map.of("followRequestId", request.getId()));
            }
            return new RelationshipActionResponseDTO(targetUserId, "REQUESTED", false, true);
        }

        this.createFollowIfMissing(currentUserId, targetUserId);
        existingRequest.filter(request -> request.getStatus() == FollowRequestStatusEnum.PENDING)
                .ifPresent(request -> {
                    request.setStatus(FollowRequestStatusEnum.ACCEPTED);
                    request.setRespondedAt(TimeUtil.getCurrentDatetime());
                    this.followRequestRepository.save(request);
                });
        this.producerService.publish(
                targetUserId,
                currentUserId,
                "FOLLOWED",
                "USER",
                targetUserId,
                null,
                "Người theo dõi mới",
                "đã bắt đầu theo dõi bạn.",
                java.util.Map.of("route", "/profile"));
        return new RelationshipActionResponseDTO(targetUserId, "FOLLOWING", true, false);
    }

    public RelationshipActionResponseDTO unfollowOrCancel(String username) {
        Integer currentUserId = this.getCurrentUserId();
        Integer targetUserId = this.getProfile(username).getUserId();
        this.ensureNotSelf(currentUserId, targetUserId);

        if (this.followRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUserId)) {
            this.followRepository.deleteByFollowerIdAndFollowingId(currentUserId, targetUserId);
            return new RelationshipActionResponseDTO(targetUserId, "UNFOLLOWED", false, false);
        }

        Optional<FollowRequestEntity> request = this.followRequestRepository
                .findBySenderIdAndReceiverId(currentUserId, targetUserId)
                .filter(item -> item.getStatus() == FollowRequestStatusEnum.PENDING);
        request.ifPresent(item -> {
            item.setStatus(FollowRequestStatusEnum.CANCELLED);
            item.setRespondedAt(TimeUtil.getCurrentDatetime());
            this.followRequestRepository.save(item);
        });
        return new RelationshipActionResponseDTO(targetUserId,
                request.isPresent() ? "CANCELLED" : "NOT_FOLLOWING", false, false);
    }

    public PageResponseDTO<FollowSummaryResponseDTO> getFollowers(int page, int size) {
        Integer currentUserId = this.getCurrentUserId();
        Page<FollowEntity> follows = this.followRepository.findAllByFollowingId(
                currentUserId, this.pageable(page, size));
        return PageResponseUtil.convert(follows.map(item -> new FollowSummaryResponseDTO(
                item.getFollowerId(), item.getCreatedAt())));
    }

    public PageResponseDTO<FollowSummaryResponseDTO> getFollowing(int page, int size) {
        Integer currentUserId = this.getCurrentUserId();
        Page<FollowEntity> follows = this.followRepository.findAllByFollowerId(
                currentUserId, this.pageable(page, size));
        return PageResponseUtil.convert(follows.map(item -> new FollowSummaryResponseDTO(
                item.getFollowingId(), item.getCreatedAt())));
    }

    public PageResponseDTO<FollowRequestSummaryResponseDTO> getIncomingFollowRequests(int page, int size) {
        Integer currentUserId = this.getCurrentUserId();
        Page<FollowRequestEntity> requests = this.followRequestRepository.findAllByReceiverIdAndStatus(
                currentUserId, FollowRequestStatusEnum.PENDING, this.pageable(page, size));
        return PageResponseUtil.convert(requests.map(this::toFollowRequestSummary));
    }

    public RelationshipActionResponseDTO acceptFollowRequest(Integer requestId) {
        Integer currentUserId = this.getCurrentUserId();
        FollowRequestEntity request = this.getIncomingRequest(requestId, currentUserId);
        this.ensureNotBlockedEitherWay(request.getSenderId(), currentUserId);
        if (request.getStatus() != FollowRequestStatusEnum.PENDING) {
            throw new FollowRequestNotPendingException();
        }

        this.createFollowIfMissing(request.getSenderId(), currentUserId);
        request.setStatus(FollowRequestStatusEnum.ACCEPTED);
        request.setRespondedAt(TimeUtil.getCurrentDatetime());
        this.followRequestRepository.save(request);
        this.producerService.publish(
                request.getSenderId(),
                currentUserId,
                "FOLLOW_ACCEPTED",
                "USER",
                currentUserId,
                request.getId(),
                "Yêu cầu theo dõi đã được chấp nhận",
                "đã chấp nhận yêu cầu theo dõi của bạn.",
                java.util.Map.of("followRequestId", request.getId()));
        return new RelationshipActionResponseDTO(request.getSenderId(), "ACCEPTED", false, false);
    }

    public RelationshipActionResponseDTO rejectFollowRequest(Integer requestId) {
        Integer currentUserId = this.getCurrentUserId();
        FollowRequestEntity request = this.getIncomingRequest(requestId, currentUserId);
        if (request.getStatus() != FollowRequestStatusEnum.PENDING) {
            throw new FollowRequestNotPendingException();
        }
        request.setStatus(FollowRequestStatusEnum.REJECTED);
        request.setRespondedAt(TimeUtil.getCurrentDatetime());
        this.followRequestRepository.save(request);
        return new RelationshipActionResponseDTO(request.getSenderId(), "REJECTED", false, false);
    }

    public RelationshipActionResponseDTO cancelFollowRequest(Integer requestId) {
        Integer currentUserId = this.getCurrentUserId();
        FollowRequestEntity request = this.followRequestRepository.findByIdAndSenderId(requestId, currentUserId)
                .orElseThrow(() -> new FollowRequestNotFoundException());
        if (request.getStatus() != FollowRequestStatusEnum.PENDING) {
            throw new FollowRequestNotPendingException();
        }
        request.setStatus(FollowRequestStatusEnum.CANCELLED);
        request.setRespondedAt(TimeUtil.getCurrentDatetime());
        this.followRequestRepository.save(request);
        return new RelationshipActionResponseDTO(request.getReceiverId(), "CANCELLED", false, false);
    }

    public RelationshipStatusResponseDTO getRelationshipStatus(String username) {
        Integer currentUserId = this.getCurrentUserId();
        Integer targetUserId = this.getProfile(username).getUserId();
        if (currentUserId.equals(targetUserId)) {
            return new RelationshipStatusResponseDTO(targetUserId, false, false, false, false);
        }
        if (this.blockRepository.existsByBlockerIdAndBlockedId(currentUserId, targetUserId)
                || this.blockRepository.existsByBlockerIdAndBlockedId(targetUserId, currentUserId)) {
            return new RelationshipStatusResponseDTO(targetUserId, false, false, false, false);
        }
        boolean following = this.followRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUserId);
        boolean followedBy = this.followRepository.existsByFollowerIdAndFollowingId(targetUserId, currentUserId);
        boolean closeFriend = this.closeFriendRepository.existsByOwnerUserIdAndMemberUserId(currentUserId, targetUserId);
        boolean requestPending = this.followRequestRepository.findBySenderIdAndReceiverId(currentUserId, targetUserId)
                .map(request -> request.getStatus() == FollowRequestStatusEnum.PENDING)
                .orElse(false);
        return new RelationshipStatusResponseDTO(targetUserId, following, followedBy, closeFriend, requestPending);
    }

    @Override
    public List<Integer> getStoryAudience(String visibility, List<Integer> ownerUserIds) {
        Integer currentUserId = this.getCurrentUserId();
        if (ValidationUtil.isNull(ownerUserIds) || ownerUserIds.isEmpty()) {
            return List.of();
        }

        List<Integer> candidates = ownerUserIds.stream()
                .filter(java.util.Objects::nonNull)
                .filter(id -> id > 0 && !id.equals(currentUserId))
                .distinct()
                .toList();
        if (candidates.isEmpty()) {
            return List.of();
        }

        Set<Integer> audience = switch (ValidationUtil.isNull(visibility) ? "" : visibility.toUpperCase()) {
            case "EVERYONE" -> candidates.stream().collect(Collectors.toSet());
            case "FOLLOWERS" -> this.followRepository
                    .findAllByFollowerIdAndFollowingIdIn(currentUserId, candidates).stream()
                    .map(FollowEntity::getFollowingId)
                    .collect(Collectors.toSet());
            case "FRIENDS" -> {
                Set<Integer> followedOwners = this.followRepository
                        .findAllByFollowerIdAndFollowingIdIn(currentUserId, candidates).stream()
                        .map(FollowEntity::getFollowingId)
                        .collect(Collectors.toSet());
                yield followedOwners.isEmpty() ? Set.of() : this.followRepository
                        .findAllByFollowerIdInAndFollowingId(List.copyOf(followedOwners), currentUserId).stream()
                        .map(FollowEntity::getFollowerId)
                        .collect(Collectors.toSet());
            }
            case "BEST_FRIENDS" -> this.closeFriendRepository
                    .findAllByOwnerUserIdInAndMemberUserId(candidates, currentUserId).stream()
                    .map(CloseFriendEntity::getOwnerUserId)
                    .collect(Collectors.toSet());
            case "NO_ONE" -> Set.of();
            default -> Set.of();
        };

        return candidates.stream()
                .filter(audience::contains)
                .filter(ownerId -> !this.blockRepository.existsByBlockerIdAndBlockedId(currentUserId, ownerId))
                .filter(ownerId -> !this.blockRepository.existsByBlockerIdAndBlockedId(ownerId, currentUserId))
                .toList();
    }

    public PageResponseDTO<CloseFriendSummaryResponseDTO> getCloseFriends(int page, int size) {
        Integer currentUserId = this.getCurrentUserId();
        Page<CloseFriendEntity> members = this.closeFriendRepository.findAllByOwnerUserId(
                currentUserId, this.pageable(page, size));
        return PageResponseUtil.convert(members.map(item -> new CloseFriendSummaryResponseDTO(
                item.getMemberUserId(), item.getCreatedAt())));
    }

    public RelationshipActionResponseDTO addCloseFriend(String username) {
        Integer currentUserId = this.getCurrentUserId();
        Integer targetUserId = this.getProfile(username).getUserId();
        this.ensureNotSelf(currentUserId, targetUserId);
        this.ensureNotBlockedEitherWay(currentUserId, targetUserId);
        if (!this.closeFriendRepository.existsByOwnerUserIdAndMemberUserId(currentUserId, targetUserId)) {
            this.closeFriendRepository.save(CloseFriendEntity.builder()
                    .ownerUserId(currentUserId)
                    .memberUserId(targetUserId)
                    .createdAt(TimeUtil.getCurrentDatetime())
                    .build());
        }
        return new RelationshipActionResponseDTO(targetUserId, "CLOSE_FRIEND", false, false);
    }

    public RelationshipActionResponseDTO removeCloseFriend(String username) {
        Integer currentUserId = this.getCurrentUserId();
        Integer targetUserId = this.getProfile(username).getUserId();
        this.ensureNotSelf(currentUserId, targetUserId);
        this.closeFriendRepository.deleteByOwnerUserIdAndMemberUserId(currentUserId, targetUserId);
        return new RelationshipActionResponseDTO(targetUserId, "REMOVED_FROM_CLOSE_FRIENDS", false, false);
    }

    public RelationshipActionResponseDTO block(String username) {
        Integer currentUserId = this.getCurrentUserId();
        Integer targetUserId = this.getProfile(username).getUserId();
        this.ensureNotSelf(currentUserId, targetUserId);
        if (!this.blockRepository.existsByBlockerIdAndBlockedId(currentUserId, targetUserId)) {
            this.blockRepository.save(BlockEntity.builder()
                    .blockerId(currentUserId)
                    .blockedId(targetUserId)
                    .createdAt(TimeUtil.getCurrentDatetime())
                    .build());
        }

        this.followRepository.deleteByFollowerIdAndFollowingId(currentUserId, targetUserId);
        this.followRepository.deleteByFollowerIdAndFollowingId(targetUserId, currentUserId);
        this.closeFriendRepository.deleteByOwnerUserIdAndMemberUserId(currentUserId, targetUserId);
        this.closeFriendRepository.deleteByOwnerUserIdAndMemberUserId(targetUserId, currentUserId);
        this.cancelPendingRequest(currentUserId, targetUserId);
        this.cancelPendingRequest(targetUserId, currentUserId);
        this.cancelPendingConversationRequest(currentUserId, targetUserId);
        this.cancelPendingConversationRequest(targetUserId, currentUserId);
        return new RelationshipActionResponseDTO(targetUserId, "BLOCKED", false, false);
    }

    public RelationshipActionResponseDTO unblock(String username) {
        Integer currentUserId = this.getCurrentUserId();
        Integer targetUserId = this.getProfile(username).getUserId();
        this.ensureNotSelf(currentUserId, targetUserId);
        this.blockRepository.deleteByBlockerIdAndBlockedId(currentUserId, targetUserId);
        return new RelationshipActionResponseDTO(targetUserId, "UNBLOCKED", false, false);
    }

    public PageResponseDTO<BlockSummaryResponseDTO> getBlockedUsers(int page, int size) {
        Integer currentUserId = this.getCurrentUserId();
        Page<BlockEntity> blockedUsers = this.blockRepository.findAllByBlockerId(
                currentUserId, this.pageable(page, size));
        return PageResponseUtil.convert(blockedUsers.map(item -> new BlockSummaryResponseDTO(
                item.getBlockedId(), item.getCreatedAt())));
    }

}
