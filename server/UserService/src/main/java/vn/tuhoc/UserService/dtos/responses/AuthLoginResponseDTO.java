package vn.tuhoc.UserService.dtos.responses;

import org.springframework.http.ResponseCookie;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthLoginResponseDTO {
    String accessToken;

    String sessionDeviceId;

    String installationId;

    String deviceName;

    String e2eeDeviceId;

    boolean mfaRequired;

    String otpPublicId;

    String otpExpiresAt;

    String otpDestination;

    @JsonIgnore
    ResponseCookie responseCookie;

    AuthUserLoginResponseDTO userLogin;
}
