package vn.tuhoc.ContentService.domains.mappers;

import org.mapstruct.Mapper;
import vn.tuhoc.ContentService.domains.entities.StorySettingEntity;
import vn.tuhoc.ContentService.dtos.responses.StorySettingDetailResponseDTO;
import vn.tuhoc.core.configs.CentralMapperConfig;

@Mapper(config = CentralMapperConfig.class)
public interface StorySettingMapper {
    StorySettingDetailResponseDTO entityToDetailResponse(StorySettingEntity storySettingEntity);
}
