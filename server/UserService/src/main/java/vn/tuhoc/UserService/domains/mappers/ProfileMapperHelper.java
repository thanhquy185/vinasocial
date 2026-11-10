package vn.tuhoc.UserService.domains.mappers;

import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.entities.ProfileEntity;
import vn.tuhoc.UserService.repositories.ProfileRepository;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileMapperHelper {
    final ProfileRepository profileRepository;

    public ProfileEntity mapToEntity(Integer id) {
        return this.profileRepository.findById(id).get();
    }
}
