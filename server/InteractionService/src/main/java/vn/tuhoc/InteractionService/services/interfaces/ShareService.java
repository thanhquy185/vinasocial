package vn.tuhoc.InteractionService.services.interfaces;

import vn.tuhoc.InteractionService.domains.enums.ShareTargetTypeEnum;
import vn.tuhoc.InteractionService.dtos.requests.ShareCreateRequestDTO;
import vn.tuhoc.InteractionService.dtos.responses.ShareSummaryResponseDTO;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;

public interface ShareService {
    ShareSummaryResponseDTO create(ShareCreateRequestDTO request);
    PageResponseDTO<ShareSummaryResponseDTO> getShares(ShareTargetTypeEnum targetType, Integer targetId, int page, int size);
    long countShares(ShareTargetTypeEnum targetType, Integer targetId);
}
