package vn.tuhoc.UserService.domains.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.tuhoc.UserService.domains.entities.ProfilePrivacySettingEntity;
import vn.tuhoc.UserService.dtos.requests.ProfilePrivacySettingCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.ProfilePrivacySettingDetailResponseDTO;
import vn.tuhoc.core.configs.CentralMapperConfig;

@Mapper(config = CentralMapperConfig.class, uses = {
                ProfileMapperHelper.class
})
public interface ProfilePrivacySettingMapper {
        ProfilePrivacySettingDetailResponseDTO entityToDetailResponse(
                        ProfilePrivacySettingEntity profilePrivacySettingEntity);

        @Mapping(target = "profile", source = "profileId")
        @Mapping(target = "profileId", ignore = true)
        ProfilePrivacySettingEntity createEntityFromRequest(
                        ProfilePrivacySettingCreateRequestDTO profilePrivacySettingCreateRequestDTO);
}
