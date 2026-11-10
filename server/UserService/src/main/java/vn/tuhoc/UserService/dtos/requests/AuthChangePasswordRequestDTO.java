package vn.tuhoc.UserService.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthChangePasswordRequestDTO {
    @NotBlank(message = "Mật khẩu hiện tại không được để trống!")
    String currentPassword;
}
