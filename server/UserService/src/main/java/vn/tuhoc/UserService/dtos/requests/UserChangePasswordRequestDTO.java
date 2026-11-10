package vn.tuhoc.UserService.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserChangePasswordRequestDTO {
    @NotBlank(message = "Mật khẩu mới không được để trống!")
    private String newPassword;

    @NotBlank(message = "Mật khẩu mới lần 2 không được để trống!")
    private String newPassword2;
}