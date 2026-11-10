package vn.tuhoc.InteractionService.services.interfaces;

import vn.tuhoc.InteractionService.dtos.responses.InteractionActionResponseDTO;
import vn.tuhoc.InteractionService.dtos.responses.PostCollectionSummaryResponseDTO;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;

public interface ReupService {
    InteractionActionResponseDTO reup(Integer postId);
    InteractionActionResponseDTO unreup(Integer postId);
    PageResponseDTO<PostCollectionSummaryResponseDTO> getMyReups(int page, int size);
    PageResponseDTO<PostCollectionSummaryResponseDTO> getReupsByUsername(String username, int page, int size);
    long countReups(Integer postId);
}
