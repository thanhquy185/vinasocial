package vn.tuhoc.UserService.services.interfaces;

import vn.tuhoc.UserService.dtos.requests.ProfilePrivacySettingCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.ProfilePrivacySettingDetailResponseDTO;

public interface ProfilePrivacySettingService {
    ProfilePrivacySettingDetailResponseDTO handleGetByProfileId(Integer profileId);

    ProfilePrivacySettingDetailResponseDTO handleCreate(
            ProfilePrivacySettingCreateRequestDTO profilePrivacySettingCreateRequestDTO);
}
