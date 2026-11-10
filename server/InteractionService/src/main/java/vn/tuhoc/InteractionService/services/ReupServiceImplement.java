package vn.tuhoc.InteractionService.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.entities.ReupEntity;
import vn.tuhoc.InteractionService.clients.UserClient;
import vn.tuhoc.InteractionService.dtos.clients.UserProfileVisibilityResponseDTO;
import vn.tuhoc.InteractionService.dtos.responses.InteractionActionResponseDTO;
import vn.tuhoc.InteractionService.dtos.responses.PostCollectionSummaryResponseDTO;
import vn.tuhoc.InteractionService.repositories.ReupRepository;
import vn.tuhoc.InteractionService.services.interfaces.ReupService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.utils.PageResponseUtil;
import vn.tuhoc.core.utils.TimeUtil;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReupServiceImplement implements ReupService {
    InteractionSequenceService sequenceService;

    UserClient userClient;

    ReupRepository reupRepository;

    private InteractionActionResponseDTO action(Integer postId, String action, boolean active) {
        return new InteractionActionResponseDTO(postId, "POST", action, active,
                this.reupRepository.countByPostId(postId));
    }

    @Override
    public InteractionActionResponseDTO reup(Integer postId) {
        InteractionServiceSupport.requirePositiveId(postId, "postId");
        Integer userId = InteractionServiceSupport.currentUserId();
        boolean alreadyActive = this.reupRepository.existsByUserIdAndPostId(userId, postId);
        if (!alreadyActive) {
            this.reupRepository.save(ReupEntity.builder()
                .id(this.sequenceService.nextId("reups"))
                    .userId(userId)
                    .postId(postId)
                    .createdAt(TimeUtil.getCurrentDatetime())
                    .build());
        }
        return this.action(postId, alreadyActive ? "ALREADY_REUPPED" : "REUPPED", true);
    }

    @Override
    public InteractionActionResponseDTO unreup(Integer postId) {
        InteractionServiceSupport.requirePositiveId(postId, "postId");
        Integer userId = InteractionServiceSupport.currentUserId();
        boolean wasActive = this.reupRepository.existsByUserIdAndPostId(userId, postId);
        if (wasActive) {
            this.reupRepository.deleteByUserIdAndPostId(userId, postId);
        }
        return this.action(postId, wasActive ? "UNREUPPED" : "NOT_REUPPED", false);
    }

    @Override
    public PageResponseDTO<PostCollectionSummaryResponseDTO> getMyReups(int page, int size) {
        Integer userId = InteractionServiceSupport.currentUserId();
        Pageable pageable = InteractionServiceSupport.pageable(page, size);
        Page<ReupEntity> reups = this.reupRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponseUtil.convert(reups.map(item -> new PostCollectionSummaryResponseDTO(
                item.getPostId(), item.getCreatedAt())));
    }

    @Override
    public PageResponseDTO<PostCollectionSummaryResponseDTO> getReupsByUsername(String username, int page, int size) {
        var response = this.userClient.getPublicProfile(username);
        UserProfileVisibilityResponseDTO profile = response == null ? null : response.getData();
        if (profile == null || profile.getUserId() == null) {
            return PageResponseUtil.convert(Page.empty(InteractionServiceSupport.pageable(page, size)));
        }
        if (!Boolean.TRUE.equals(profile.getCanViewReposts())) {
            throw new AccessDeniedException("Danh sách bài viết đăng lại của tài khoản này không được chia sẻ công khai.");
        }

        Pageable pageable = InteractionServiceSupport.pageable(page, size);
        Page<ReupEntity> reups = this.reupRepository.findAllByUserIdOrderByCreatedAtDesc(profile.getUserId(), pageable);
        return PageResponseUtil.convert(reups.map(item -> new PostCollectionSummaryResponseDTO(
                item.getPostId(), item.getCreatedAt())));
    }

    @Override
    public long countReups(Integer postId) {
        InteractionServiceSupport.requirePositiveId(postId, "postId");
        return this.reupRepository.countByPostId(postId);
    }
}
