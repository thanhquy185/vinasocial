package vn.tuhoc.UserService.dtos.responses;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Convert;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.converters.UserRoleConverter;
import vn.tuhoc.UserService.domains.converters.UserStatusConverter;
import vn.tuhoc.UserService.domains.enums.UserRoleEnum;
import vn.tuhoc.UserService.domains.enums.UserStatusEnum;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailResponseDTO {
    Integer id;

    String publicId;

    String createdAt;

    String lastLoginAt;

    @Convert(converter = UserRoleConverter.class)
    UserRoleEnum role;

    @Convert(converter = UserStatusConverter.class)
    UserStatusEnum status;

    List<UsernameDetailResponseDTO> usernames;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    AuthOtpDetailResponseDTO authOtp;

    ProfileDetailResponseDTO profile;
}
