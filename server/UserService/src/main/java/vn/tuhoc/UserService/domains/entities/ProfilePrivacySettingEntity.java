package vn.tuhoc.UserService.domains.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.converters.ProfilePrivacySettingPermissionConverter;
import vn.tuhoc.UserService.domains.enums.ProfilePrivacySettingPermissionEnum;

@Entity
@Table(name = "profile_privacy_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfilePrivacySettingEntity {
    @Id
    @Column(columnDefinition = "INT")
    Integer profileId;

    @Column(columnDefinition = "BIT", nullable = false)
    Boolean isPrivate;

    @Column(columnDefinition = "VARCHAR(12)", nullable = false)
    @Convert(converter = ProfilePrivacySettingPermissionConverter.class)
    ProfilePrivacySettingPermissionEnum showProfilePermission;

    @Column(columnDefinition = "VARCHAR(12)", nullable = false)
    @Convert(converter = ProfilePrivacySettingPermissionConverter.class)
    ProfilePrivacySettingPermissionEnum showFollowingsPermission;

    @Column(columnDefinition = "VARCHAR(12)", nullable = false)
    @Convert(converter = ProfilePrivacySettingPermissionConverter.class)
    ProfilePrivacySettingPermissionEnum showFollowersPermission;

    @Column(columnDefinition = "VARCHAR(12)", nullable = false)
    @Convert(converter = ProfilePrivacySettingPermissionConverter.class)
    ProfilePrivacySettingPermissionEnum showHighlightStoriesPermission;

    @Column(columnDefinition = "VARCHAR(12)", nullable = false)
    @Convert(converter = ProfilePrivacySettingPermissionConverter.class)
    ProfilePrivacySettingPermissionEnum showPostsPermission;

    @Column(columnDefinition = "VARCHAR(12)", nullable = false)
    @Convert(converter = ProfilePrivacySettingPermissionConverter.class)
    ProfilePrivacySettingPermissionEnum showRepostsPermission;

    @Column(columnDefinition = "VARCHAR(12)", nullable = false)
    @Convert(converter = ProfilePrivacySettingPermissionConverter.class)
    ProfilePrivacySettingPermissionEnum showSavesPermission;

    @Column(columnDefinition = "VARCHAR(12)", nullable = false)
    @Convert(converter = ProfilePrivacySettingPermissionConverter.class)
    ProfilePrivacySettingPermissionEnum sendMessagePermission;

    @OneToOne
    @MapsId
    @JoinColumn(name = "profile_id")
    ProfileEntity profile;
}
