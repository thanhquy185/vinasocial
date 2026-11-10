package vn.tuhoc.UserService.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.enums.AuthOtpPurposeEnum;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthOtpCreateRequestDTO {
    @NotNull(message = "Mã người dùng không được để trống!")
    Integer userId;

    @NotNull(message = "Mã tên người dùng không được để trống!")
    Integer usernameId;

    @NotNull(message = "Thời gian tạo không được để trống!")
    String createdAt;

    @NotNull(message = "Thời gian hết hạn không được để trống!")
    String expiredAt;

    @NotNull(message = "Mục đích không được để trống!")
    AuthOtpPurposeEnum purpose;
}
