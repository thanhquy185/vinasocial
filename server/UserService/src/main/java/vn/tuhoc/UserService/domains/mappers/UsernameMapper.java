package vn.tuhoc.UserService.domains.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.tuhoc.UserService.domains.entities.UsernameEntity;
import vn.tuhoc.UserService.dtos.requests.UsernameCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.UsernameDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.UsernameInfoResponseDTO;
import vn.tuhoc.UserService.dtos.responses.UsernameSummaryResponseDTO;
import vn.tuhoc.core.configs.CentralMapperConfig;

@Mapper(config = CentralMapperConfig.class, uses = {
        UserMapperHelper2.class,
})
public interface UsernameMapper {
    UsernameDetailResponseDTO entityToDetailResponse(UsernameEntity usernameEntity);

    UsernameSummaryResponseDTO entityToSummaryResponse(UsernameEntity usernameEntity);

    UsernameInfoResponseDTO entityToInfoResponse(UsernameEntity usernameEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "authOtps", ignore = true)
    UsernameEntity createEntityFromRequest(UsernameCreateRequestDTO usernameCreateRequestDTO);
}