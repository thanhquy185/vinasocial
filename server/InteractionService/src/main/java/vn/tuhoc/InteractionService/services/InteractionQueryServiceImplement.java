package vn.tuhoc.InteractionService.services;

import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.enums.CommentStatusEnum;
import vn.tuhoc.InteractionService.domains.enums.CommentTargetTypeEnum;
import vn.tuhoc.InteractionService.domains.enums.LikeTargetTypeEnum;
import vn.tuhoc.InteractionService.domains.enums.ShareTargetTypeEnum;
import vn.tuhoc.InteractionService.domains.enums.ViewTargetTypeEnum;
import vn.tuhoc.InteractionService.dtos.responses.TargetStatsResponseDTO;
import vn.tuhoc.InteractionService.repositories.CommentRepository;
import vn.tuhoc.InteractionService.repositories.LikeRepository;
import vn.tuhoc.InteractionService.repositories.ReupRepository;
import vn.tuhoc.InteractionService.repositories.SaveRepository;
import vn.tuhoc.InteractionService.repositories.ShareRepository;
import vn.tuhoc.InteractionService.repositories.ViewRepository;
import vn.tuhoc.InteractionService.services.interfaces.InteractionQueryService;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InteractionQueryServiceImplement implements InteractionQueryService {
    ViewRepository viewRepository;
    LikeRepository likeRepository;
    CommentRepository commentRepository;
    ShareRepository shareRepository;
    SaveRepository saveRepository;
    ReupRepository reupRepository;

    @Override
    public TargetStatsResponseDTO getTargetStats(ShareTargetTypeEnum targetType, Integer targetId) {
        InteractionServiceSupport.requirePositiveId(targetId, "targetId");
        Integer userId = InteractionServiceSupport.currentUserId();
        LikeTargetTypeEnum likeTargetType = LikeTargetTypeEnum.valueOf(targetType.name());
        long commentCount = targetType == ShareTargetTypeEnum.POST
                ? this.commentRepository.countByTargetTypeAndTargetIdAndStatus(
                        CommentTargetTypeEnum.POST, targetId, CommentStatusEnum.ACTIVE)
                : 0;
        return new TargetStatsResponseDTO(
                targetId,
                targetType.name(),
                this.viewRepository.countByTargetTypeAndTargetId(ViewTargetTypeEnum.valueOf(targetType.name()), targetId),
                this.likeRepository.countByTargetTypeAndTargetId(likeTargetType, targetId),
                commentCount,
                this.shareRepository.countByTargetTypeAndTargetId(targetType, targetId),
                targetType == ShareTargetTypeEnum.POST ? this.reupRepository.countByPostId(targetId) : 0,
                this.likeRepository.existsByUserIdAndTargetTypeAndTargetId(userId, likeTargetType, targetId),
                targetType == ShareTargetTypeEnum.POST && this.saveRepository.existsByUserIdAndPostId(userId, targetId),
                targetType == ShareTargetTypeEnum.POST && this.reupRepository.existsByUserIdAndPostId(userId, targetId));
    }
}
