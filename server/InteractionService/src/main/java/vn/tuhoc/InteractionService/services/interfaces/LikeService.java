package vn.tuhoc.InteractionService.services.interfaces;

import vn.tuhoc.InteractionService.domains.enums.LikeTargetTypeEnum;
import vn.tuhoc.InteractionService.dtos.responses.InteractionActionResponseDTO;
import vn.tuhoc.InteractionService.dtos.responses.LikeSummaryResponseDTO;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;

public interface LikeService {
    InteractionActionResponseDTO like(LikeTargetTypeEnum targetType, Integer targetId);
    InteractionActionResponseDTO unlike(LikeTargetTypeEnum targetType, Integer targetId);
    InteractionActionResponseDTO getLikeStatus(LikeTargetTypeEnum targetType, Integer targetId);
    PageResponseDTO<LikeSummaryResponseDTO> getLikes(LikeTargetTypeEnum targetType, Integer targetId, int page, int size);
    long countLikes(LikeTargetTypeEnum targetType, Integer targetId);
}
