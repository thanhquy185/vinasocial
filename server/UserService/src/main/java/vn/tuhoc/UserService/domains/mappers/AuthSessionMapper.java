package vn.tuhoc.UserService.domains.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.tuhoc.UserService.domains.entities.AuthSessionEntity;
import vn.tuhoc.UserService.dtos.requests.AuthSessionCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.AuthSessionDetailResponseDTO;
import vn.tuhoc.core.configs.CentralMapperConfig;

@Mapper(config = CentralMapperConfig.class, uses = {
                UserMapperHelper.class
})
public interface AuthSessionMapper {
        AuthSessionDetailResponseDTO entityToDetailResponse(AuthSessionEntity authSessionEntity);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "user", source = "userId")
        AuthSessionEntity createEntityFromRequest(AuthSessionCreateRequestDTO authSessionCreateRequestDTO);
}
