package vn.tuhoc.UserService.dtos.requests;

import jakarta.persistence.Convert;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.converters.UserStatusConverter;
import vn.tuhoc.UserService.domains.enums.UserStatusEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateStatusRequestDTO {
    @NotNull(message = "Trạng thái không được để trống!")
    @Convert(converter = UserStatusConverter.class)
    UserStatusEnum status;
}
