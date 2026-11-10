package vn.tuhoc.UserService.services.interfaces;

import vn.tuhoc.UserService.dtos.requests.ProfileCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.ProfilePublicResponseDTO;
import vn.tuhoc.UserService.dtos.requests.ProfileUpdateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.ProfilePersonalInformationUpdateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.ProfilePrivacySettingUpdateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.ProfileNotificationSettingUpdateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.ProfilePrivacySettingDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileNotificationSettingDetailResponseDTO;

public interface ProfileService {
    ProfileDetailResponseDTO handleGetById(Integer id);

    ProfileDetailResponseDTO handleGetByUserId(Integer userId);

    ProfileDetailResponseDTO handleGetByPhone(String phone);

    ProfileDetailResponseDTO handleGetByEmail(String email);

    ProfileDetailResponseDTO handleGetByUsername(String username);

    ProfilePublicResponseDTO handleGetPublicByUsername(String username);

    ProfileDetailResponseDTO handleUpdateCurrentProfile(ProfileUpdateRequestDTO request);

    ProfileDetailResponseDTO handleUpdateCurrentPersonalInformation(
            ProfilePersonalInformationUpdateRequestDTO request);

    ProfilePrivacySettingDetailResponseDTO handleUpdateCurrentPrivacy(
            ProfilePrivacySettingUpdateRequestDTO request);

    ProfileNotificationSettingDetailResponseDTO handleUpdateCurrentNotifications(
            ProfileNotificationSettingUpdateRequestDTO request);

    ProfileDetailResponseDTO handleCreate(ProfileCreateRequestDTO profileCreateRequestDTO);
}
