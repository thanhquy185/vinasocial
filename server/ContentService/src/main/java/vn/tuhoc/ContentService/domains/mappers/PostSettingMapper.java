package vn.tuhoc.ContentService.domains.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.tuhoc.ContentService.domains.entities.PostSettingEntity;
import vn.tuhoc.ContentService.dtos.requests.PostSettingCreateRequestDTO;
import vn.tuhoc.ContentService.dtos.responses.PostSettingDetailResponseDTO;
import vn.tuhoc.core.configs.CentralMapperConfig;

@Mapper(config = CentralMapperConfig.class, uses = {
        PostMapperHelper.class
})
public interface PostSettingMapper {
    PostSettingDetailResponseDTO entityToDetailResponse(PostSettingEntity postSettingEntity);

    @Mapping(target = "post", source = "postId")
    @Mapping(target = "postId", ignore = true)
    PostSettingEntity createEntityFromRequest(PostSettingCreateRequestDTO postSettingCreateRequestDTO);
}
