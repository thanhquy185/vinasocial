package vn.tuhoc.UserService.domains.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import vn.tuhoc.UserService.domains.entities.UserEntity;
import vn.tuhoc.UserService.dtos.requests.UserCreateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.UserDeleteRequestDTO;
import vn.tuhoc.UserService.dtos.responses.UserDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.UserInfoResponseDTO;
import vn.tuhoc.UserService.dtos.responses.UserSummaryResponseDTO;
import vn.tuhoc.core.configs.CentralMapperConfig;

@Mapper(config = CentralMapperConfig.class, uses = {
                UsernameMapperHelper.class
})
public interface UserMapper {
        @Mapping(target = "profile", ignore = true)
        UserDetailResponseDTO entityToDetailResponse(UserEntity userEntity);

        UserSummaryResponseDTO entityToSummaryResponse(UserEntity userEntity);

        UserInfoResponseDTO entityToInfoResponse(UserEntity userEntity);

        @Mapping(target = "id", ignore = true)
        UserEntity createEntityFromRequest(UserCreateRequestDTO userCreateRequestDTO);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "publicId", ignore = true)
        @Mapping(target = "createdAt", ignore = true)
        @Mapping(target = "lastLoginAt", ignore = true)
        @Mapping(target = "role", ignore = true)
        @Mapping(target = "password", ignore = true)
        void deleteEntityFromRequest(
                        UserDeleteRequestDTO userDeleteRequestDTO,
                        @MappingTarget UserEntity userEntity);
}
