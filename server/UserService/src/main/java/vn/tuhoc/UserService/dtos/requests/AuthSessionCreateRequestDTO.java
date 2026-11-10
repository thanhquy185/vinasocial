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
public class AuthSessionCreateRequestDTO {
    @NotNull(message = "Mã người dùng không được để trống!")
    Integer userId;

    @NotNull(message = "Thời gian tạo không được để trống!")
    String createdAt;

    @NotNull(message = "Thời gian hết hạn không được để trống!")
    String expiredAt;

    @NotNull(message = "Tên đăng nhập không được để trống!")
    String username;

    @NotNull(message = "Mã cài đặt thiết bị không được để trống!")
    String installationId;

    @NotNull(message = "Mã phiên thiết bị không được để trống!")
    String sessionDeviceId;

    @NotNull(message = "Tên thiết bị không được để trống!")
    String deviceName;

    @NotNull(message = "Thời gian hoạt động gần nhất không được để trống!")
    String lastSeenAt;

    String e2eeDeviceId;

    @NotNull(message = "Mã refresh token không được để trống!")
    String refreshToken;
}
