package vn.tuhoc.UserService.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthOtpResetPasswordRequestDTO {
    @NotNull(message = "Mã không được để trống!")
    String publicId;

    @NotNull(message = "Mật khẩu mới không được để trống!")
    String newPassword;

    @NotNull(message = "Xác nhận mật khẩu mới không được để trống!")
    String confirmPassword;
}
