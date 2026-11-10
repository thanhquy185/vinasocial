package vn.tuhoc.ContentService.domains.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.tuhoc.ContentService.domains.entities.StoryEntity;
import vn.tuhoc.ContentService.dtos.requests.StoryCreateRequestDTO;
import vn.tuhoc.ContentService.dtos.responses.StoryDetailResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.StorySummaryResponseDTO;
import vn.tuhoc.core.configs.CentralMapperConfig;

@Mapper(config = CentralMapperConfig.class, uses = {
        StoryStatsMapper.class,
        StorySettingMapper.class,
})
public interface StoryMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "media", ignore = true)
    StoryDetailResponseDTO entityToDetailResponse(StoryEntity storyEntity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "media", ignore = true)
    StorySummaryResponseDTO entityToSummaryResponse(StoryEntity storyEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "expiredAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "moderationStatus", ignore = true)
    @Mapping(target = "stats", ignore = true)
    @Mapping(target = "setting", ignore = true)
    StoryEntity createEntityFromRequest(StoryCreateRequestDTO request);
}
