package vn.tuhoc.UserService.services.interfaces;

import java.util.List;

import vn.tuhoc.UserService.domains.entities.UserEntity;
import vn.tuhoc.UserService.dtos.requests.UserChangePasswordRequestDTO;
import vn.tuhoc.UserService.dtos.requests.UserCreateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.UserDeleteRequestDTO;
import vn.tuhoc.UserService.dtos.requests.UserUpdateStatusRequestDTO;
import vn.tuhoc.UserService.dtos.responses.UserDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.UserSummaryResponseDTO;
import vn.tuhoc.UserService.dtos.responses.ProfilePublicResponseDTO;
import vn.tuhoc.UserService.dtos.requests.ProfileUpdateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.ProfilePersonalInformationUpdateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.ProfilePrivacySettingUpdateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.ProfileNotificationSettingUpdateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.ProfilePrivacySettingDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileNotificationSettingDetailResponseDTO;
import vn.tuhoc.UserService.repositories.criteria.UserCriteria;
import vn.tuhoc.core.dtos.clients.UserInfoReqClientDTO;
import vn.tuhoc.core.dtos.clients.UserInfoResClientDTO;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;

public interface UserService {
    List<UserInfoResClientDTO> handleGetInfo(UserInfoReqClientDTO userInfoReqClientDTO);

    UserDetailResponseDTO handleGetById(Integer id);

    UserDetailResponseDTO handleGetByPublicId(String publicId);

    UserDetailResponseDTO handleGetByUsername(String username);

    ProfilePublicResponseDTO handleGetPublicProfileByUsername(String username);

    List<ProfilePublicResponseDTO> handleSearchPublicProfiles(String query);

    Object handleGetProfileForViewerByUsername(String username);

    ProfileDetailResponseDTO handleUpdateCurrentProfile(ProfileUpdateRequestDTO request);

    ProfileDetailResponseDTO handleUpdateCurrentPersonalInformation(
            ProfilePersonalInformationUpdateRequestDTO request);

    ProfilePrivacySettingDetailResponseDTO handleUpdateCurrentPrivacy(
            ProfilePrivacySettingUpdateRequestDTO request);

    ProfileNotificationSettingDetailResponseDTO handleUpdateCurrentNotifications(
            ProfileNotificationSettingUpdateRequestDTO request);

    UserEntity handleGetByUsernameToLogin(String username);

    UserEntity handleGetByUsernameAndPassword(String username, String password);

    PageResponseDTO<UserSummaryResponseDTO> handleGetSummary(UserCriteria userCriteria);

    UserDetailResponseDTO handleCreate(UserCreateRequestDTO userCreateRequestDTO);

    UserDetailResponseDTO handleCreateMember(UserCreateRequestDTO userCreateRequestDTO);

    UserDetailResponseDTO handleUpdateStatusById(
            Integer id,
            UserUpdateStatusRequestDTO request);

    UserDetailResponseDTO handleChangePassword(Integer id, UserChangePasswordRequestDTO userChangePasswordRequestDTO);

    UserDetailResponseDTO handleDelete(Integer id, UserDeleteRequestDTO userDeleteRequestDTO);

    String userTestConnect(String fullname, String username);

}
