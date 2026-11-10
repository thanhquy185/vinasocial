package vn.tuhoc.UserService.dtos.requests;

import jakarta.validation.constraints.NotBlank;
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
public class AuthSessionE2eeLinkRequestDTO {
    @NotBlank(message = "Mã thiết bị E2EE không được để trống!")
    @Size(max = 64, message = "Mã thiết bị E2EE không hợp lệ!")
    String e2eeDeviceId;
}
