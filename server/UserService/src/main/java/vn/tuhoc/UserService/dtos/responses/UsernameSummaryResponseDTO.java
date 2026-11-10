package vn.tuhoc.UserService.dtos.responses;

import jakarta.persistence.Convert;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.converters.UsernameTypeConverter;
import vn.tuhoc.UserService.domains.enums.UsernameTypeEnum;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsernameSummaryResponseDTO {
    @Convert(converter = UsernameTypeConverter.class)
    UsernameTypeEnum type;

    String value;
}
