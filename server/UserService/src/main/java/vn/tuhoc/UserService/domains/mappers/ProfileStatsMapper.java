package vn.tuhoc.UserService.domains.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.tuhoc.UserService.domains.entities.ProfileStatsEntity;
import vn.tuhoc.UserService.dtos.requests.ProfileStatsCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileStatsDetailResponseDTO;
import vn.tuhoc.core.configs.CentralMapperConfig;

@Mapper(config = CentralMapperConfig.class, uses = {
        ProfileMapperHelper.class
})
public interface ProfileStatsMapper {
    ProfileStatsDetailResponseDTO entityToDetailResponse(ProfileStatsEntity profileStatsEntity);

    @Mapping(target = "profile", source = "profileId")
    @Mapping(target = "profileId", ignore = true)
    ProfileStatsEntity createEntityFromRequest(ProfileStatsCreateRequestDTO profileStatsCreateRequestDTO);
}
