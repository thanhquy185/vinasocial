package vn.tuhoc.UserService.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.clients.NotificationClient;
import vn.tuhoc.UserService.dtos.requests.UserChangePasswordRequestDTO;
import vn.tuhoc.UserService.dtos.requests.UserCreateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.UserDeleteRequestDTO;
import vn.tuhoc.UserService.dtos.requests.UserUpdateStatusRequestDTO;
import vn.tuhoc.UserService.dtos.responses.UserDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.UserSummaryResponseDTO;
import vn.tuhoc.UserService.dtos.requests.UserTestConnectRequestDTO;
import vn.tuhoc.UserService.dtos.responses.ProfilePublicResponseDTO;
import vn.tuhoc.UserService.dtos.requests.ProfileUpdateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.ProfilePersonalInformationUpdateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.ProfilePrivacySettingUpdateRequestDTO;
import vn.tuhoc.UserService.dtos.requests.ProfileNotificationSettingUpdateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.ProfilePrivacySettingDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileNotificationSettingDetailResponseDTO;
import vn.tuhoc.UserService.repositories.criteria.UserCriteria;
import vn.tuhoc.UserService.services.interfaces.UserService;
import vn.tuhoc.core.dtos.clients.UserInfoReqClientDTO;
import vn.tuhoc.core.dtos.clients.UserInfoResClientDTO;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
        UserService userService;
        NotificationClient notificationClient;

        @GetMapping("/user-test-connect")
        public String getMethodName(
                        @RequestParam(value = "fullname") String fullname,
                        @RequestParam(value = "username") String username) {

                String result = this.userService.userTestConnect(fullname, username);
                if (result.equalsIgnoreCase("Đăng ký thành công!")) {
                        UserTestConnectRequestDTO userTestConnectRequestDTO = new UserTestConnectRequestDTO();
                        userTestConnectRequestDTO.setFullname(fullname);
                        userTestConnectRequestDTO.setUsername(username);

                        this.notificationClient.notificationTestConnect(userTestConnectRequestDTO);
                }

                return result;
        }

        @PostMapping(value = "/info", consumes = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<List<UserInfoResClientDTO>> getInfo(
                        @RequestBody UserInfoReqClientDTO userInfoReqClientDTO) {
                return ResponseEntity.ok(this.userService.handleGetInfo(userInfoReqClientDTO));
        }

        @GetMapping("/profile/{username}")
        public ResponseEntity<RestResponseDTO<Object>> getPublicProfile(
                        @PathVariable("username") String username) {
                Object profile = this.userService.handleGetProfileForViewerByUsername(username);
                return RestResponseUtil.ok("Truy vấn trang cá nhân thành công!", profile);
        }

        @GetMapping("/search")
        public ResponseEntity<RestResponseDTO<List<ProfilePublicResponseDTO>>> searchProfiles(
                        @RequestParam("query") String query) {
                return RestResponseUtil.ok("Tìm kiếm tài khoản thành công!",
                                this.userService.handleSearchPublicProfiles(query));
        }

        @PutMapping(value = "/profile", consumes = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<RestResponseDTO<vn.tuhoc.UserService.dtos.responses.ProfileDetailResponseDTO>> updateCurrentProfile(
                        @RequestBody @Valid ProfileUpdateRequestDTO request) {
                return RestResponseUtil.ok("Cập nhật trang cá nhân thành công!",
                                this.userService.handleUpdateCurrentProfile(request));
        }

        @PutMapping(value = "/profile/personal-information", consumes = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<RestResponseDTO<vn.tuhoc.UserService.dtos.responses.ProfileDetailResponseDTO>> updateCurrentPersonalInformation(
                        @RequestBody @Valid ProfilePersonalInformationUpdateRequestDTO request) {
                return RestResponseUtil.ok("Cập nhật thông tin cá nhân thành công!",
                                this.userService.handleUpdateCurrentPersonalInformation(request));
        }

        @PutMapping(value = "/profile/privacy", consumes = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<RestResponseDTO<ProfilePrivacySettingDetailResponseDTO>> updateCurrentPrivacy(
                        @RequestBody @Valid ProfilePrivacySettingUpdateRequestDTO request) {
                return RestResponseUtil.ok("Cập nhật quyền riêng tư thành công!",
                                this.userService.handleUpdateCurrentPrivacy(request));
        }

        @PutMapping(value = "/profile/notifications", consumes = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<RestResponseDTO<ProfileNotificationSettingDetailResponseDTO>> updateCurrentNotifications(
                        @RequestBody @Valid ProfileNotificationSettingUpdateRequestDTO request) {
                return RestResponseUtil.ok("Cập nhật cài đặt thông báo thành công!",
                                this.userService.handleUpdateCurrentNotifications(request));
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<RestResponseDTO<UserDetailResponseDTO>> getDetailById(
                        @PathVariable("id") Integer id) {
                UserDetailResponseDTO userDetail = this.userService.handleGetById(id);

                return RestResponseUtil.ok(
                                "Truy vấn người dùng theo mã người dùng thành công!",
                                userDetail);
        }

        @GetMapping("")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<RestResponseDTO<PageResponseDTO<UserSummaryResponseDTO>>> getSummary(
                        UserCriteria userCriteria) {
                PageResponseDTO<UserSummaryResponseDTO> userSummary = this.userService.handleGetSummary(userCriteria);

                return RestResponseUtil.ok(
                                "Truy vấn danh sách người dùng thành công!",
                                userSummary);
        }

        @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<RestResponseDTO<UserDetailResponseDTO>> create(
                        @RequestBody @Valid UserCreateRequestDTO userCreateRequestDTO) {
                UserDetailResponseDTO userCreated = this.userService.handleCreate(userCreateRequestDTO);

                return RestResponseUtil.created(
                                "Thêm người dùng thành công!",
                                userCreated);
        }

        @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<RestResponseDTO<UserDetailResponseDTO>> changePassword(
                        @PathVariable("id") Integer id,
                        UserChangePasswordRequestDTO userChangePasswordRequestDTO) {
                UserDetailResponseDTO userChangePassword = this.userService.handleChangePassword(
                                id,
                                userChangePasswordRequestDTO);

                return RestResponseUtil.ok(
                                "Thay đổi mật khẩu người dùng thành công!",
                                userChangePassword);
        }

        @PatchMapping(value = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE)
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<RestResponseDTO<UserDetailResponseDTO>> updateStatusById(
                        @PathVariable("id") Integer id,
                        @RequestBody @Valid UserUpdateStatusRequestDTO request) {
                UserDetailResponseDTO userUpdated = this.userService.handleUpdateStatusById(id, request);

                return RestResponseUtil.ok(
                                "Cập nhật trạng thái người dùng thành công!",
                                userUpdated);
        }

        @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<RestResponseDTO<UserDetailResponseDTO>> delete(
                        @PathVariable("id") Integer id,
                        @RequestBody @Valid UserDeleteRequestDTO userDeleteRequestDTO) {
                UserDetailResponseDTO userDeleted = this.userService.handleDelete(id, userDeleteRequestDTO);

                return RestResponseUtil.ok(
                                "Cập nhật trạng thái người dùng thành công!",
                                userDeleted);
        }
}
