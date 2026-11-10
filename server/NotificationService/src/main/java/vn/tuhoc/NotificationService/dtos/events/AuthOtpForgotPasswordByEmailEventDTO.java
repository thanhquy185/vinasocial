package vn.tuhoc.NotificationService.dtos.events;

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
public class AuthOtpForgotPasswordByEmailEventDTO {
    Integer userId;

    String expiredAt;

    String code;

    String email;

    String firstName;

    String lastName;
}
