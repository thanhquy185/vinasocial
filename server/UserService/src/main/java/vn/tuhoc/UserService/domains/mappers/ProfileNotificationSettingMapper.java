package vn.tuhoc.UserService.domains.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.tuhoc.UserService.domains.entities.ProfileNotificationSettingEntity;
import vn.tuhoc.UserService.dtos.requests.ProfileNotificationSettingCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileNotificationSettingDetailResponseDTO;
import vn.tuhoc.core.configs.CentralMapperConfig;

@Mapper(config = CentralMapperConfig.class, uses = {
                ProfileMapperHelper.class
})
public interface ProfileNotificationSettingMapper {
        ProfileNotificationSettingDetailResponseDTO entityToDetailResponse(
                        ProfileNotificationSettingEntity profileNotificationSettingEntity);

        @Mapping(target = "profile", source = "profileId")
        @Mapping(target = "profileId", ignore = true)
        ProfileNotificationSettingEntity createEntityFromRequest(
                        ProfileNotificationSettingCreateRequestDTO profileNotificationSettingCreateRequestDTO);
}
