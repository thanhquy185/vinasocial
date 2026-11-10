package vn.tuhoc.UserService.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class AuthLoginRequestDTO {
    @NotBlank(message = "Tên đăng nhập không được để trống!")
    String identifier;

    @NotBlank(message = "Mật khẩu không được để trống!")
    String password;

    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89aAbB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$", message = "Mã cài đặt thiết bị không hợp lệ!")
    String installationId;

    @Size(max = 120, message = "Tên thiết bị không hợp lệ!")
    String deviceName;
}
