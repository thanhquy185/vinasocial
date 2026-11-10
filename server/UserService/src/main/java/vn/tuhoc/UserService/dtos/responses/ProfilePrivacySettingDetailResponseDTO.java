package vn.tuhoc.UserService.dtos.responses;

import jakarta.persistence.Convert;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.converters.ProfilePrivacySettingPermissionConverter;
import vn.tuhoc.UserService.domains.enums.ProfilePrivacySettingPermissionEnum;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfilePrivacySettingDetailResponseDTO {
    Boolean isPrivate;

    @Convert(converter = ProfilePrivacySettingPermissionConverter.class)
    ProfilePrivacySettingPermissionEnum showProfilePermission;

    @Convert(converter = ProfilePrivacySettingPermissionConverter.class)
    ProfilePrivacySettingPermissionEnum showFollowingsPermission;

    @Convert(converter = ProfilePrivacySettingPermissionConverter.class)
    ProfilePrivacySettingPermissionEnum showFollowersPermission;

    @Convert(converter = ProfilePrivacySettingPermissionConverter.class)
    ProfilePrivacySettingPermissionEnum showHighlightStoriesPermission;

    @Convert(converter = ProfilePrivacySettingPermissionConverter.class)
    ProfilePrivacySettingPermissionEnum showPostsPermission;

    @Convert(converter = ProfilePrivacySettingPermissionConverter.class)
    ProfilePrivacySettingPermissionEnum showRepostsPermission;

    @Convert(converter = ProfilePrivacySettingPermissionConverter.class)
    ProfilePrivacySettingPermissionEnum showSavesPermission;

    @Convert(converter = ProfilePrivacySettingPermissionConverter.class)
    ProfilePrivacySettingPermissionEnum sendMessagePermission;
}
