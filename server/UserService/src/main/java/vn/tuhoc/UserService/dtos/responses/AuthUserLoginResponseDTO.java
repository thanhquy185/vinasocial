package vn.tuhoc.UserService.dtos.responses;

import jakarta.persistence.Convert;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.converters.UserRoleConverter;
import vn.tuhoc.UserService.domains.converters.UserStatusConverter;
import vn.tuhoc.UserService.domains.enums.UserRoleEnum;
import vn.tuhoc.UserService.domains.enums.UserStatusEnum;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthUserLoginResponseDTO {
    Integer id;

    String publicId;

    String username;

    @Convert(converter = UserRoleConverter.class)
    UserRoleEnum role;

    @Convert(converter = UserStatusConverter.class)
    UserStatusEnum status;
}
