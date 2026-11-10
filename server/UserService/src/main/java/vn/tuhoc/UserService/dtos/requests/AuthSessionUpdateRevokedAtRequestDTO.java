package vn.tuhoc.UserService.dtos.requests;

import jakarta.validation.constraints.NotNull;
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
public class AuthSessionUpdateRevokedAtRequestDTO {
    @NotNull(message = "Mã người dùng không được để trống!")
    Integer userId;

    @NotNull(message = "Mã refresh token không được để trống!")
    String refreshToken;
}