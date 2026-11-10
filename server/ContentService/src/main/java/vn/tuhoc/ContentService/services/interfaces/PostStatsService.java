package vn.tuhoc.ContentService.services.interfaces;

import vn.tuhoc.ContentService.dtos.requests.PostStatsCreateRequestDTO;
import vn.tuhoc.ContentService.dtos.responses.PostStatsDetailResponseDTO;

public interface PostStatsService {
    PostStatsDetailResponseDTO handleGetByPostId(Integer postId);

    PostStatsDetailResponseDTO handleCreate(PostStatsCreateRequestDTO postStatsCreateRequestDTO);
}
