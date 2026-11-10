package vn.tuhoc.UserService.domains.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.tuhoc.UserService.domains.entities.ProfileEntity;
import vn.tuhoc.UserService.dtos.requests.ProfileCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileDetailResponseDTO;
import vn.tuhoc.core.configs.CentralMapperConfig;

@Mapper(config = CentralMapperConfig.class, uses = {
        UserMapperHelper2.class,
        ProfileStatsMapperHelper.class,
        ProfilePrivacySettingMapperHelper.class,
        ProfileNotificationSettingMapperHelper.class,
        // ProfileLinkMapperHelper.class
})
public interface ProfileMapper {
    @Mapping(target = "avatar", ignore = true)
    ProfileDetailResponseDTO entityToDetailResponse(ProfileEntity profileEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "alias", ignore = true)
    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "note", ignore = true)
    ProfileEntity createEntityFromRequest(ProfileCreateRequestDTO profileCreateRequestDTO);
}
