package vn.tuhoc.InteractionService.services.interfaces;

import vn.tuhoc.InteractionService.domains.enums.ShareTargetTypeEnum;
import vn.tuhoc.InteractionService.dtos.responses.TargetStatsResponseDTO;

public interface InteractionQueryService {
    TargetStatsResponseDTO getTargetStats(ShareTargetTypeEnum targetType, Integer targetId);
}
