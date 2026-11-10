package vn.tuhoc.UserService.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthMfaPasswordRequestDTO {
    @NotBlank(message = "Vui lòng nhập mật khẩu để xác nhận!")
    String password;
}
