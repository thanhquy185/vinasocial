package vn.tuhoc.UserService.dtos.requests;

import jakarta.persistence.Convert;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.converters.ProfileGenderConverter;
import vn.tuhoc.UserService.domains.enums.ProfileGenderEnum;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthRegisterRequestDTO {
    String createdAt;

    @NotBlank(message = "Tên đăng nhập không được để trống!")
    String identifier;

    @NotBlank(message = "Mật khẩu không được để trống!")
    String password;

    @NotBlank(message = "Họ không được để trống!")
    String firstName;

    @NotBlank(message = "Tên không được để trống!")
    String lastName;

    @NotNull(message = "Ngày sinh không được để trống!")
    String birthdate;

    @NotNull(message = "Giới tính không được để trống!")
    @Convert(converter = ProfileGenderConverter.class)
    ProfileGenderEnum gender;

    @NotBlank(message = "Tên người dùng không được để trống!")
    String username;
}
