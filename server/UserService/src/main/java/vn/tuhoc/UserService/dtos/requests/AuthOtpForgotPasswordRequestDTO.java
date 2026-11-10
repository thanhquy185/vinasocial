package vn.tuhoc.UserService.dtos.requests;

import jakarta.persistence.Convert;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.converters.UsernameTypeConverter;
import vn.tuhoc.UserService.domains.enums.UsernameTypeEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthOtpForgotPasswordRequestDTO {
    @NotNull(message = "Loại tên đăng nhập không được để trống!")
    @Convert(converter = UsernameTypeConverter.class)
    UsernameTypeEnum usernameType;

    @NotNull(message = "Giá trị tên đăng nhập không được để trống!")
    String usernameValue;
}