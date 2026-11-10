package vn.tuhoc.UserService.dtos.responses;

import java.util.List;

import jakarta.persistence.Convert;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.converters.ProfileGenderConverter;
import vn.tuhoc.UserService.domains.enums.ProfileGenderEnum;
import vn.tuhoc.core.dtos.clients.MediaInfoResClientDTO;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileDetailResponseDTO {
    Integer id;

    String firstName;

    String lastName;

    String birthdate;

    @Convert(converter = ProfileGenderConverter.class)
    ProfileGenderEnum gender;

    String phone;

    String email;

    MediaInfoResClientDTO avatar;

    String username;

    String alias;

    String bio;

    String note;

    ProfileStatsDetailResponseDTO stats;

    ProfileNotificationSettingDetailResponseDTO notificationSetting;

    ProfilePrivacySettingDetailResponseDTO privacySetting;

    List<ProfileLinkDetailResponseDTO> links;
}
