package vn.tuhoc.ContentService.services.interfaces;

import java.util.List;

import vn.tuhoc.ContentService.dtos.requests.PostCreateRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.PostUpdateRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.PostUpdateModerationStatusRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.PostUpdateStatusRequestDTO;
import vn.tuhoc.ContentService.dtos.responses.PostDetailResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.PostSummaryResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.ProfilePostResponseDTO;
import vn.tuhoc.ContentService.repositories.criteria.PostCriteria;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;

public interface PostService {
    PostDetailResponseDTO handleGetById(Integer id);

    PostDetailResponseDTO handleGetByPublicId(String publicId);

    PageResponseDTO<PostSummaryResponseDTO> handleGetSummary(PostCriteria postCriteria);

    PageResponseDTO<PostDetailResponseDTO> handleGetExplorePosts(
            int page, int size, String query, Boolean reelsOnly);

    List<ProfilePostResponseDTO> handleGetProfilePosts(String username);

    List<PostDetailResponseDTO> handleGetTaggedPosts(String username);

    PostDetailResponseDTO handleCreate(PostCreateRequestDTO postCreateRequestDTO);

    PostDetailResponseDTO handleUpdateById(Integer id, PostUpdateRequestDTO request);

    void handleRemove(Integer id);

    PostDetailResponseDTO handleUpdateStatusById(Integer id, PostUpdateStatusRequestDTO request);

    PostDetailResponseDTO handleUpdateModerationStatusById(
            Integer id, PostUpdateModerationStatusRequestDTO request);
}
