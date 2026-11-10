package vn.tuhoc.ContentService.domains.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.tuhoc.ContentService.domains.entities.PostStatsEntity;
import vn.tuhoc.ContentService.dtos.requests.PostStatsCreateRequestDTO;
import vn.tuhoc.ContentService.dtos.responses.PostStatsDetailResponseDTO;
import vn.tuhoc.core.configs.CentralMapperConfig;

@Mapper(config = CentralMapperConfig.class, uses = {
        PostMapperHelper.class
})
public interface PostStatsMapper {
    PostStatsDetailResponseDTO entityToDetailResponse(PostStatsEntity postStatsEntity);

    @Mapping(target = "post", source = "postId")
    @Mapping(target = "postId", ignore = true)
    PostStatsEntity createEntityFromRequest(PostStatsCreateRequestDTO postStatsCreateRequestDTO);
}
