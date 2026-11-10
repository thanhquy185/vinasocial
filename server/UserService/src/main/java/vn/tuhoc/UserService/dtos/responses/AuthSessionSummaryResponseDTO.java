package vn.tuhoc.UserService.dtos.responses;

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
public class AuthSessionSummaryResponseDTO {
    String sessionDeviceId;
    String installationId;
    String deviceName;
    String createdAt;
    String expiredAt;
    String lastSeenAt;
    String revokedAt;
    String e2eeDeviceId;
    boolean current;
    boolean active;
}
