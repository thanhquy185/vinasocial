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
import vn.tuhoc.UserService.domains.converters.ProfileGenderConverter;
import vn.tuhoc.UserService.domains.enums.ProfileGenderEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileCreateRequestDTO {
    @NotNull(message = "Mã người dùng không được để trống!")
    Integer userId;

    @NotBlank(message = "Họ không được để trống!")
    String firstName;

    @NotBlank(message = "Tên không được để trống!")
    String lastName;

    @NotNull(message = "Ngày sinh không được để trống!")
    String birthdate;

    @NotNull(message = "Giới tính không được để trống!")
    @Convert(converter = ProfileGenderConverter.class)
    ProfileGenderEnum gender;

    String phone;

    String email;

    @NotBlank(message = "Tên người dùng không được để trống!")
    String username;
}
