package vn.tuhoc.UserService.dtos.events;

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
public class AuthOtpSecurityEventDTO {
    Integer userId;
    String expiredAt;
    String code;
    String email;
    String phone;
    String firstName;
    String lastName;
    String purpose;
}
