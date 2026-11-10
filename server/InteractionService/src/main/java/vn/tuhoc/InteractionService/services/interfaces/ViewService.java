package vn.tuhoc.InteractionService.services.interfaces;

import java.util.List;

import vn.tuhoc.InteractionService.domains.enums.ViewTargetTypeEnum;
import vn.tuhoc.InteractionService.dtos.responses.ViewSummaryResponseDTO;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;

public interface ViewService {
    void recordView(ViewTargetTypeEnum targetType, Integer targetId);
    PageResponseDTO<ViewSummaryResponseDTO> getViewers(ViewTargetTypeEnum targetType, Integer targetId, int page, int size);
    long countViews(ViewTargetTypeEnum targetType, Integer targetId);
    List<Integer> getMyViewedTargetIds(ViewTargetTypeEnum targetType, List<Integer> targetIds);
}
