package vn.tuhoc.UserService.domains.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.tuhoc.UserService.domains.entities.ProfileLinkEntity;
import vn.tuhoc.UserService.dtos.requests.ProfileLinkCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileLinkDetailResponseDTO;
import vn.tuhoc.core.configs.CentralMapperConfig;

@Mapper(config = CentralMapperConfig.class, uses = {
        ProfileMapperHelper.class
})
public interface ProfileLinkMapper {
    ProfileLinkDetailResponseDTO entityToDetailResponse(ProfileLinkEntity profileLinkEntity);

    @Mapping(target = "profile", source = "profileId")
    ProfileLinkEntity createEntityFromRequest(ProfileLinkCreateRequestDTO profileLinkCreateRequestDTO);
}
