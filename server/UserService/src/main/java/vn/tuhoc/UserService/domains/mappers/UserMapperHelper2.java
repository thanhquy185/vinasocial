package vn.tuhoc.UserService.domains.mappers;

import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.entities.UserEntity;
import vn.tuhoc.UserService.repositories.UserRepository;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserMapperHelper2 {
    final UserRepository userRepository;

    public UserEntity mapToEntity(Integer id) {
        return this.userRepository.findById(id).get();
    }
}
