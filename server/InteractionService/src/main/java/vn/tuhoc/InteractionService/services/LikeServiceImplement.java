package vn.tuhoc.InteractionService.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.entities.LikeEntity;
import vn.tuhoc.InteractionService.domains.enums.CommentStatusEnum;
import vn.tuhoc.InteractionService.domains.enums.LikeTargetTypeEnum;
import vn.tuhoc.InteractionService.dtos.responses.InteractionActionResponseDTO;
import vn.tuhoc.InteractionService.dtos.responses.LikeSummaryResponseDTO;
import vn.tuhoc.InteractionService.repositories.CommentRepository;
import vn.tuhoc.InteractionService.repositories.LikeRepository;
import vn.tuhoc.InteractionService.services.interfaces.LikeService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.utils.PageResponseUtil;
import vn.tuhoc.core.utils.TimeUtil;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LikeServiceImplement implements LikeService {
    InteractionSequenceService sequenceService;

    LikeRepository likeRepository;
    CommentRepository commentRepository;

    private void ensureCommentTargetActive(LikeTargetTypeEnum targetType, Integer targetId) {
        if (targetType == LikeTargetTypeEnum.COMMENT
                && this.commentRepository.findByIdAndStatus(targetId, CommentStatusEnum.ACTIVE).isEmpty()) {
            throw InteractionServiceSupport.notFound("COMMENT_NOT_FOUND", "Không tìm thấy bình luận đang hoạt động.");
        }
    }

    private InteractionActionResponseDTO action(LikeTargetTypeEnum targetType, Integer targetId,
            String action, boolean active) {
        return new InteractionActionResponseDTO(targetId, targetType.name(), action, active,
                this.likeRepository.countByTargetTypeAndTargetId(targetType, targetId));
    }

    @Override
    public InteractionActionResponseDTO like(LikeTargetTypeEnum targetType, Integer targetId) {
        Integer userId = InteractionServiceSupport.currentUserId();
        InteractionServiceSupport.requirePositiveId(targetId, "targetId");
        this.ensureCommentTargetActive(targetType, targetId);
        boolean alreadyLiked = this.likeRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
        if (!alreadyLiked) {
            this.likeRepository.save(LikeEntity.builder()
                .id(this.sequenceService.nextId("likes"))
                    .userId(userId)
                    .targetId(targetId)
                    .targetType(targetType)
                    .createdAt(TimeUtil.getCurrentDatetime())
                    .build());
            if (targetType == LikeTargetTypeEnum.COMMENT) {
                this.commentRepository.incrementLikeCount(targetId, CommentStatusEnum.ACTIVE);
            }
        }
        return this.action(targetType, targetId, alreadyLiked ? "ALREADY_LIKED" : "LIKED", true);
    }

    @Override
    public InteractionActionResponseDTO unlike(LikeTargetTypeEnum targetType, Integer targetId) {
        Integer userId = InteractionServiceSupport.currentUserId();
        InteractionServiceSupport.requirePositiveId(targetId, "targetId");
        boolean wasLiked = this.likeRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
        if (wasLiked) {
            this.likeRepository.deleteByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
            if (targetType == LikeTargetTypeEnum.COMMENT) {
                this.commentRepository.decrementLikeCount(targetId);
            }
        }
        return this.action(targetType, targetId, wasLiked ? "UNLIKED" : "NOT_LIKED", false);
    }

    @Override
    public InteractionActionResponseDTO getLikeStatus(LikeTargetTypeEnum targetType, Integer targetId) {
        Integer userId = InteractionServiceSupport.currentUserId();
        InteractionServiceSupport.requirePositiveId(targetId, "targetId");
        boolean active = this.likeRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
        return this.action(targetType, targetId, active ? "LIKED" : "NOT_LIKED", active);
    }

    @Override
    public PageResponseDTO<LikeSummaryResponseDTO> getLikes(
            LikeTargetTypeEnum targetType, Integer targetId, int page, int size) {
        InteractionServiceSupport.requirePositiveId(targetId, "targetId");
        Pageable pageable = InteractionServiceSupport.pageable(page, size);
        Page<LikeEntity> likes = this.likeRepository.findAllByTargetTypeAndTargetId(targetType, targetId, pageable);
        return PageResponseUtil.convert(likes.map(item -> new LikeSummaryResponseDTO(
                item.getId(), item.getUserId(), item.getTargetId(), item.getTargetType().name(), item.getCreatedAt())));
    }

    @Override
    public long countLikes(LikeTargetTypeEnum targetType, Integer targetId) {
        InteractionServiceSupport.requirePositiveId(targetId, "targetId");
        return this.likeRepository.countByTargetTypeAndTargetId(targetType, targetId);
    }
}
