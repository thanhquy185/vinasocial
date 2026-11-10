package vn.tuhoc.UserService.dtos.requests;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.enums.ProfileNotificationSettingUpdateTypeEnum;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileNotificationSettingUpdateRequestDTO {
    ProfileNotificationSettingUpdateTypeEnum type;
    JsonNode value;
}
