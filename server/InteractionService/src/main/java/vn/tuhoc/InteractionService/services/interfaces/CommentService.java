package vn.tuhoc.InteractionService.services.interfaces;

import vn.tuhoc.InteractionService.domains.enums.CommentStatusEnum;
import vn.tuhoc.InteractionService.domains.enums.CommentTargetTypeEnum;
import vn.tuhoc.InteractionService.dtos.requests.CommentCreateRequestDTO;
import vn.tuhoc.InteractionService.dtos.requests.CommentUpdateRequestDTO;
import vn.tuhoc.InteractionService.dtos.responses.CommentDetailResponseDTO;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;

public interface CommentService {
    CommentDetailResponseDTO create(CommentCreateRequestDTO request);
    CommentDetailResponseDTO update(Integer commentId, CommentUpdateRequestDTO request);
    CommentDetailResponseDTO delete(Integer commentId);
    CommentDetailResponseDTO updateStatus(Integer commentId, CommentStatusEnum status);
    CommentDetailResponseDTO getById(Integer commentId);
    PageResponseDTO<CommentDetailResponseDTO> getComments(CommentTargetTypeEnum targetType, Integer targetId, int page, int size);
    PageResponseDTO<CommentDetailResponseDTO> getReplies(Integer commentId, int page, int size);
    long countComments(CommentTargetTypeEnum targetType, Integer targetId);
}
