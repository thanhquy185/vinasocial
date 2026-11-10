package vn.tuhoc.UserService.services;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.entities.ProfileNotificationSettingEntity;
import vn.tuhoc.UserService.domains.mappers.ProfileNotificationSettingMapper;
import vn.tuhoc.UserService.dtos.requests.ProfileNotificationSettingCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileNotificationSettingDetailResponseDTO;
import vn.tuhoc.UserService.exceptions.ProfileNotificationSettingNotFoundByProfileIdException;
import vn.tuhoc.UserService.repositories.ProfileNotificationSettingRepository;
import vn.tuhoc.UserService.services.interfaces.ProfileNotificationSettingService;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileNotificationSettingServiceImplement implements ProfileNotificationSettingService {
    ProfileNotificationSettingRepository profileNotificationSettingRepository;
    ProfileNotificationSettingMapper profileNotificationSettingMapper;

    private ProfileNotificationSettingEntity getByProfileId(Integer profileId) {
        return this.profileNotificationSettingRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotificationSettingNotFoundByProfileIdException(profileId));
    }

    @Override
    public ProfileNotificationSettingDetailResponseDTO handleGetByProfileId(Integer profileId) {
        return this.profileNotificationSettingMapper.entityToDetailResponse(this.getByProfileId(profileId));
    }

    @Override
    public ProfileNotificationSettingDetailResponseDTO handleCreate(
            ProfileNotificationSettingCreateRequestDTO profileNotificationSettingCreateRequestDTO) {
        ProfileNotificationSettingEntity profileNotificationSettingEntity = this.profileNotificationSettingMapper
                .createEntityFromRequest(profileNotificationSettingCreateRequestDTO);

        return this.profileNotificationSettingMapper.entityToDetailResponse(
                this.profileNotificationSettingRepository.save(profileNotificationSettingEntity));
    }

}
