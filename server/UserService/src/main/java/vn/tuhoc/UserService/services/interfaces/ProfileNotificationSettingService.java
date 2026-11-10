package vn.tuhoc.UserService.services.interfaces;

import vn.tuhoc.UserService.dtos.requests.ProfileNotificationSettingCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileNotificationSettingDetailResponseDTO;

public interface ProfileNotificationSettingService {
    ProfileNotificationSettingDetailResponseDTO handleGetByProfileId(Integer profileId);

    ProfileNotificationSettingDetailResponseDTO handleCreate(
            ProfileNotificationSettingCreateRequestDTO profileNotificationSettingCreateRequestDTO);
}
