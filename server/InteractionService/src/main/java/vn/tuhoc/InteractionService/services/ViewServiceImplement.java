package vn.tuhoc.InteractionService.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.entities.ViewEntity;
import vn.tuhoc.InteractionService.domains.enums.ViewTargetTypeEnum;
import vn.tuhoc.InteractionService.dtos.responses.ViewSummaryResponseDTO;
import vn.tuhoc.InteractionService.repositories.ViewRepository;
import vn.tuhoc.InteractionService.services.interfaces.ViewService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.utils.PageResponseUtil;
import vn.tuhoc.core.utils.TimeUtil;
import vn.tuhoc.core.utils.ValidationUtil;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ViewServiceImplement implements ViewService {
    InteractionSequenceService sequenceService;

    ViewRepository viewRepository;

    @Override
    public void recordView(ViewTargetTypeEnum targetType, Integer targetId) {
        InteractionServiceSupport.requirePositiveId(targetId, "targetId");
        this.viewRepository.save(ViewEntity.builder()
                .id(this.sequenceService.nextId("views"))
                .userId(InteractionServiceSupport.currentUserId())
                .targetId(targetId)
                .targetType(targetType)
                .createdAt(TimeUtil.getCurrentDatetime())
                .build());
    }

    @Override
    public PageResponseDTO<ViewSummaryResponseDTO> getViewers(
            ViewTargetTypeEnum targetType, Integer targetId, int page, int size) {
        InteractionServiceSupport.requirePositiveId(targetId, "targetId");
        Pageable pageable = InteractionServiceSupport.pageable(page, size);
        Page<ViewEntity> views = this.viewRepository.findAllByTargetTypeAndTargetId(targetType, targetId, pageable);
        return PageResponseUtil.convert(views.map(item -> new ViewSummaryResponseDTO(
                item.getId(), item.getUserId(), item.getTargetId(), item.getTargetType().name(), item.getCreatedAt())));
    }

    @Override
    public long countViews(ViewTargetTypeEnum targetType, Integer targetId) {
        InteractionServiceSupport.requirePositiveId(targetId, "targetId");
        return this.viewRepository.countByTargetTypeAndTargetId(targetType, targetId);
    }

    @Override
    public List<Integer> getMyViewedTargetIds(ViewTargetTypeEnum targetType, List<Integer> targetIds) {
        if (ValidationUtil.isNull(targetIds) || targetIds.isEmpty()) {
            return List.of();
        }
        List<Integer> validTargetIds = targetIds.stream()
                .filter(ValidationUtil::nonNull)
                .filter(targetId -> targetId > 0)
                .distinct()
                .toList();
        if (validTargetIds.isEmpty()) {
            return List.of();
        }
        Integer userId = InteractionServiceSupport.currentUserId();
        return this.viewRepository.findAllByUserIdAndTargetTypeAndTargetIdIn(
                        userId, targetType, validTargetIds).stream()
                .map(ViewEntity::getTargetId)
                .distinct()
                .toList();
    }
}
