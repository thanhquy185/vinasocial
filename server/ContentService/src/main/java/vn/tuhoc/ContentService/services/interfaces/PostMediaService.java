package vn.tuhoc.ContentService.services.interfaces;

import java.util.List;

import vn.tuhoc.ContentService.dtos.requests.PostMediaCreateRequestDTO;
import vn.tuhoc.ContentService.dtos.responses.PostMediaDetailResponseDTO;

public interface PostMediaService {
    PostMediaDetailResponseDTO handleGetById(Integer id);

    PostMediaDetailResponseDTO handleGetByPostId(Integer postId);

    PostMediaDetailResponseDTO handleGetByMediaId(String mediaId);

    PostMediaDetailResponseDTO handleCreate(PostMediaCreateRequestDTO postMediaCreateRequestDTO);

    List<PostMediaDetailResponseDTO> handleCreateAll(List<PostMediaCreateRequestDTO> postMediaCreateRequestDTOs);
}
