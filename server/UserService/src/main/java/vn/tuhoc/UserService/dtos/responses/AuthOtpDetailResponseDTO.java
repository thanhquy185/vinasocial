package vn.tuhoc.UserService.dtos.responses;

import jakarta.persistence.Convert;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.converters.AuthOtpPurposeConverter;
import vn.tuhoc.UserService.domains.enums.AuthOtpPurposeEnum;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthOtpDetailResponseDTO {
    Integer id;

    String publicId;

    UsernameDetailResponseDTO username;

    String createdAt;

    String expiredAt;

    String verifiedAt;

    @Convert(converter = AuthOtpPurposeConverter.class)
    AuthOtpPurposeEnum purpose;

    @JsonIgnore
    String code;
}
