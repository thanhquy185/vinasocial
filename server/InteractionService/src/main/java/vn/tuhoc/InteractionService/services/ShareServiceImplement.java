package vn.tuhoc.InteractionService.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.entities.ShareEntity;
import vn.tuhoc.InteractionService.domains.enums.ShareTargetTypeEnum;
import vn.tuhoc.InteractionService.dtos.requests.ShareCreateRequestDTO;
import vn.tuhoc.InteractionService.dtos.responses.ShareSummaryResponseDTO;
import vn.tuhoc.InteractionService.repositories.ShareRepository;
import vn.tuhoc.InteractionService.services.interfaces.ShareService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.utils.PageResponseUtil;
import vn.tuhoc.core.utils.TimeUtil;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShareServiceImplement implements ShareService {
    InteractionSequenceService sequenceService;

    ShareRepository shareRepository;

    private ShareSummaryResponseDTO toSummary(ShareEntity item) {
        return new ShareSummaryResponseDTO(item.getId(), item.getUserId(), item.getTargetId(),
                item.getTargetType().name(), item.getCreatedAt(), item.getShareType().name());
    }

    @Override
    public ShareSummaryResponseDTO create(ShareCreateRequestDTO request) {
        InteractionServiceSupport.requirePositiveId(request.getTargetId(), "targetId");
        ShareEntity share = this.shareRepository.save(ShareEntity.builder()
                .id(this.sequenceService.nextId("shares"))
                .userId(InteractionServiceSupport.currentUserId())
                .targetId(request.getTargetId())
                .targetType(request.getTargetType())
                .createdAt(TimeUtil.getCurrentDatetime())
                .shareType(request.getType())
                .build());
        return this.toSummary(share);
    }

    @Override
    public PageResponseDTO<ShareSummaryResponseDTO> getShares(
            ShareTargetTypeEnum targetType, Integer targetId, int page, int size) {
        InteractionServiceSupport.requirePositiveId(targetId, "targetId");
        Pageable pageable = InteractionServiceSupport.pageable(page, size);
        Page<ShareEntity> shares = this.shareRepository.findAllByTargetTypeAndTargetId(targetType, targetId, pageable);
        return PageResponseUtil.convert(shares.map(this::toSummary));
    }

    @Override
    public long countShares(ShareTargetTypeEnum targetType, Integer targetId) {
        InteractionServiceSupport.requirePositiveId(targetId, "targetId");
        return this.shareRepository.countByTargetTypeAndTargetId(targetType, targetId);
    }
}
