package vn.tuhoc.UserService.services;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.entities.ProfilePrivacySettingEntity;
import vn.tuhoc.UserService.domains.mappers.ProfilePrivacySettingMapper;
import vn.tuhoc.UserService.dtos.requests.ProfilePrivacySettingCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.ProfilePrivacySettingDetailResponseDTO;
import vn.tuhoc.UserService.exceptions.ProfilePrivacySettingNotFoundByProfileIdException;
import vn.tuhoc.UserService.repositories.ProfilePrivacySettingRepository;
import vn.tuhoc.UserService.services.interfaces.ProfilePrivacySettingService;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfilePrivacySettingServiceImplement implements ProfilePrivacySettingService {
    ProfilePrivacySettingRepository profilePrivacySettingRepository;
    ProfilePrivacySettingMapper profilePrivacySettingMapper;

    private ProfilePrivacySettingEntity getByProfileId(Integer profileId) {
        return this.profilePrivacySettingRepository.findById(profileId)
                .orElseThrow(() -> new ProfilePrivacySettingNotFoundByProfileIdException(profileId));
    }

    @Override
    public ProfilePrivacySettingDetailResponseDTO handleGetByProfileId(Integer profileId) {
        return this.profilePrivacySettingMapper.entityToDetailResponse(this.getByProfileId(profileId));
    }

    @Override
    public ProfilePrivacySettingDetailResponseDTO handleCreate(
            ProfilePrivacySettingCreateRequestDTO profilePrivacySettingCreateRequestDTO) {
        ProfilePrivacySettingEntity profilePrivacySettingEntity = this.profilePrivacySettingMapper
                .createEntityFromRequest(profilePrivacySettingCreateRequestDTO);

        return this.profilePrivacySettingMapper.entityToDetailResponse(
                this.profilePrivacySettingRepository.save(profilePrivacySettingEntity));
    }

}
