package vn.tuhoc.UserService.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthConfirmPasswordChangeRequestDTO {
    @NotBlank(message = "Mã yêu cầu không được để trống!")
    String publicId;

    @NotBlank(message = "Mã OTP không được để trống!")
    @Pattern(regexp = "^\\d{6}$", message = "Mã OTP phải gồm đúng 6 chữ số.")
    String code;

    @NotBlank(message = "Mật khẩu mới không được để trống!")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
            message = "Mật khẩu mới cần ít nhất 8 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt (@$!%*?&).")
    String newPassword;

    @NotBlank(message = "Xác nhận mật khẩu mới không được để trống!")
    String confirmPassword;
}
