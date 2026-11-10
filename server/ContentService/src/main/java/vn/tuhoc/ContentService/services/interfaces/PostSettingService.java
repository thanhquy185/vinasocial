package vn.tuhoc.ContentService.services.interfaces;

import vn.tuhoc.ContentService.dtos.requests.PostSettingCreateRequestDTO;
import vn.tuhoc.ContentService.dtos.responses.PostSettingDetailResponseDTO;

public interface PostSettingService {
    PostSettingDetailResponseDTO handleGetByPostId(Integer postId);

    PostSettingDetailResponseDTO handleCreate(PostSettingCreateRequestDTO postSettingCreateRequestDTO);
}
