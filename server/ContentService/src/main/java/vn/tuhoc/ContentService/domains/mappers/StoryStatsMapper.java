package vn.tuhoc.ContentService.domains.mappers;

import org.mapstruct.Mapper;
import vn.tuhoc.ContentService.domains.entities.StoryStatsEntity;
import vn.tuhoc.ContentService.dtos.responses.StoryStatsDetailResponseDTO;
import vn.tuhoc.core.configs.CentralMapperConfig;

@Mapper(config = CentralMapperConfig.class)
public interface StoryStatsMapper {
    StoryStatsDetailResponseDTO entityToDetailResponse(StoryStatsEntity storyStatsEntity);
}
