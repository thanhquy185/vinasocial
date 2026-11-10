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
public class UsernameCreateRequestDTO {
    @NotNull(message = "Mã người dùng không được để trống!")
    Integer userId;

    @NotNull(message = "Loại không được để trống!")
    @Convert(converter = UsernameTypeConverter.class)
    UsernameTypeEnum type;

    @NotNull(message = "Giá trị không được để trống!")
    String value;

    @NotNull(message = "Trạng thái xác thực không được để trống!")
    Boolean isVerify;

    @NotNull(message = "Trạng thái sử dụng không được để trống!")
    Boolean isActive;
}
