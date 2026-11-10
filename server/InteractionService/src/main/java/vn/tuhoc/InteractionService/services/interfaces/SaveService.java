package vn.tuhoc.InteractionService.services.interfaces;

import vn.tuhoc.InteractionService.dtos.responses.InteractionActionResponseDTO;
import vn.tuhoc.InteractionService.dtos.responses.PostCollectionSummaryResponseDTO;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;

public interface SaveService {
    InteractionActionResponseDTO save(Integer postId);
    InteractionActionResponseDTO unsave(Integer postId);
    InteractionActionResponseDTO getSaveStatus(Integer postId);
    PageResponseDTO<PostCollectionSummaryResponseDTO> getMySaves(int page, int size);
    long countSaves(Integer postId);
}
