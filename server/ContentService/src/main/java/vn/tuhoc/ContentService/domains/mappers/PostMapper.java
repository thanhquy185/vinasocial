package vn.tuhoc.ContentService.domains.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.tuhoc.ContentService.domains.entities.PostEntity;
import vn.tuhoc.ContentService.dtos.requests.PostCreateRequestDTO;
import vn.tuhoc.ContentService.dtos.responses.PostDetailResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.PostSummaryResponseDTO;
import vn.tuhoc.core.configs.CentralMapperConfig;

@Mapper(config = CentralMapperConfig.class, uses = {
        PostStatsMapperHelper.class,
        PostSettingMapperHelper.class,
        PostMediaMapperHelper.class
})
public interface PostMapper {
    @Mapping(target = "taggedUsers", ignore = true)
    PostDetailResponseDTO entityToDetailResponse(PostEntity postEntity);

    PostSummaryResponseDTO entityToSummaryResponse(PostEntity postEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    PostEntity createEntityFromRequest(PostCreateRequestDTO postCreateRequestDTO);
}
