package vn.tuhoc.UserService.domains.mappers;

import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.entities.ProfilePrivacySettingEntity;
import vn.tuhoc.UserService.dtos.responses.ProfilePrivacySettingDetailResponseDTO;
import vn.tuhoc.UserService.repositories.ProfilePrivacySettingRepository;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfilePrivacySettingMapperHelper {
    final ProfilePrivacySettingRepository profilePrivacySettingRepository;
    final ProfilePrivacySettingMapper profilePrivacySettingMapper;

    public ProfilePrivacySettingEntity mapToEntity(Integer id) {
        return this.profilePrivacySettingRepository.findById(id).get();
    }

    public ProfilePrivacySettingDetailResponseDTO mapToDetailResponse(
            ProfilePrivacySettingEntity profilePrivacySettingEntity) {
        return this.profilePrivacySettingMapper.entityToDetailResponse(profilePrivacySettingEntity);
    }
}
