package vn.tuhoc.UserService.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.enums.ProfilePrivacySettingPermissionEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfilePrivacySettingCreateRequestDTO {
    @NotNull(message = "Mã thông tin không được để trống!")
    Integer profileId;

    @NotNull(message = "Trạng thái riêng tư không được để trống!")
    Boolean isPrivate;

    @NotNull(message = "Quyền xem thông tin không được để trống!")
    ProfilePrivacySettingPermissionEnum showProfilePermission;

    @NotNull(message = "Quyền xem danh sách đang theo dõi không được để trống!")
    ProfilePrivacySettingPermissionEnum showFollowingsPermission;

    @NotNull(message = "Quyền xem danh sách người theo dõi không được để trống!")
    ProfilePrivacySettingPermissionEnum showFollowersPermission;

    @NotNull(message = "Quyền xem tin nổi bật không được để trống!")
    ProfilePrivacySettingPermissionEnum showHighlightStoriesPermission;

    @NotNull(message = "Quyền xem bài viết không được để trống!")
    ProfilePrivacySettingPermissionEnum showPostsPermission;

    @NotNull(message = "Quyền xem bài đăng lại không được để trống!")
    ProfilePrivacySettingPermissionEnum showRepostsPermission;

    @NotNull(message = "Quyền xem bài viết đã lưu không được để trống!")
    ProfilePrivacySettingPermissionEnum showSavesPermission;

    @NotNull(message = "Quyền gửi tin nhắn không được để trống!")
    ProfilePrivacySettingPermissionEnum sendMessagePermission;
}
