package vn.tuhoc.UserService.dtos.requests;

import jakarta.persistence.Convert;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.converters.UserRoleConverter;
import vn.tuhoc.UserService.domains.converters.UserStatusConverter;
import vn.tuhoc.UserService.domains.enums.UserRoleEnum;
import vn.tuhoc.UserService.domains.enums.UserStatusEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequestDTO {
    @NotNull(message = "Quyền không được để trống!")
    @Convert(converter = UserRoleConverter.class)
    UserRoleEnum role;

    @NotBlank(message = "Tên tài khoản không được để trống!")
    String username;

    @NotBlank(message = "Mật khẩu không được để trống!")
    String password;

    @NotNull(message = "Trạng thái không được để trống!")
    @Convert(converter = UserStatusConverter.class)
    UserStatusEnum status;
}
