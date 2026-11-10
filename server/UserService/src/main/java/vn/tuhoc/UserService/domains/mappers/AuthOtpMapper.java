package vn.tuhoc.UserService.domains.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.tuhoc.UserService.domains.entities.AuthOtpEntity;
import vn.tuhoc.UserService.dtos.requests.AuthOtpCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.AuthOtpDetailResponseDTO;
import vn.tuhoc.core.configs.CentralMapperConfig;

@Mapper(config = CentralMapperConfig.class, uses = {
                UserMapperHelper.class,
                UsernameMapperHelper.class
})
public interface AuthOtpMapper {
        AuthOtpDetailResponseDTO entityToDetailResponse(AuthOtpEntity authOtpEntity);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "user", source = "userId")
        @Mapping(target = "username", source = "usernameId")
        @Mapping(target = "code", ignore = true)
        AuthOtpEntity createEntityFromRequest(AuthOtpCreateRequestDTO authOtpCreateRequestDTO);
}
