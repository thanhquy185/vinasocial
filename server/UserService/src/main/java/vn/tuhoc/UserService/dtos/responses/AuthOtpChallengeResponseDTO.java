package vn.tuhoc.UserService.dtos.responses;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.enums.AuthOtpPurposeEnum;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthOtpChallengeResponseDTO {
    String publicId;
    String destination;
    String createdAt;
    String expiredAt;
    AuthOtpPurposeEnum purpose;
}
