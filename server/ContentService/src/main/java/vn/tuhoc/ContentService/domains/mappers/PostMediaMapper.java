package vn.tuhoc.ContentService.domains.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.tuhoc.ContentService.domains.entities.PostMediaEntity;
import vn.tuhoc.ContentService.dtos.requests.PostMediaCreateRequestDTO;
import vn.tuhoc.ContentService.dtos.responses.PostMediaDetailResponseDTO;
import vn.tuhoc.core.configs.CentralMapperConfig;

@Mapper(config = CentralMapperConfig.class, uses = {
        PostMapperHelper.class
})
public interface PostMediaMapper {
    PostMediaDetailResponseDTO entityToDetailResponse(PostMediaEntity postMediaEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", source = "postId")
    PostMediaEntity createEntityFromRequest(PostMediaCreateRequestDTO postMediaCreateRequestDTO);
}
