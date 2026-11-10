package vn.tuhoc.UserService.domains.mappers;

import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.entities.ProfileNotificationSettingEntity;
import vn.tuhoc.UserService.dtos.responses.ProfileNotificationSettingDetailResponseDTO;
import vn.tuhoc.UserService.repositories.ProfileNotificationSettingRepository;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileNotificationSettingMapperHelper {
    final ProfileNotificationSettingRepository profileNotificationSettingRepository;
    final ProfileNotificationSettingMapper profileNotificationSettingMapper;

    public ProfileNotificationSettingEntity mapToEntity(Integer id) {
        return this.profileNotificationSettingRepository.findById(id).get();
    }

    public ProfileNotificationSettingDetailResponseDTO mapToDetailResponse(
            ProfileNotificationSettingEntity profileNotificationSettingEntity) {
        return this.profileNotificationSettingMapper.entityToDetailResponse(profileNotificationSettingEntity);
    }
}
