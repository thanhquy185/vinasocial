package vn.tuhoc.UserService.dtos.requests;

import jakarta.validation.constraints.NotBlank;
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
public class ProfileLinkCreateRequestDTO {
    @NotNull(message = "Mã thông tin không được để trống!")
    Integer profileId;

    @NotBlank(message = "Tiêu đề không được để trống!")
    String title;

    @NotBlank(message = "Đường dẫn không được để trống!")
    String url;
}
