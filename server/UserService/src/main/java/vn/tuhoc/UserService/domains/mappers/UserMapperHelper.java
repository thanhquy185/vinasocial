package vn.tuhoc.UserService.domains.mappers;

import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.entities.UserEntity;
import vn.tuhoc.UserService.dtos.responses.UserDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.UserInfoResponseDTO;
import vn.tuhoc.UserService.dtos.responses.UserSummaryResponseDTO;
import vn.tuhoc.UserService.repositories.UserRepository;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserMapperHelper {
    final UserRepository userRepository;
    final UserMapper userMapper;

    public UserEntity mapToEntity(Integer id) {
        return this.userRepository.findById(id).get();
    }

    public UserDetailResponseDTO mapToDetailResponse(UserEntity userEntity) {
        return this.userMapper.entityToDetailResponse(userEntity);
    }

    public UserSummaryResponseDTO mapToSummaryResponse(UserEntity userEntity) {
        return this.userMapper.entityToSummaryResponse(userEntity);
    }

    public UserInfoResponseDTO mapToInfoResponse(UserEntity userEntity) {
        return this.userMapper.entityToInfoResponse(userEntity);
    }
}
