package vn.tuhoc.UserService.dtos.responses;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthSessionDetailResponseDTO {
    Integer id;

    String createdAt;

    String expiredAt;

    String revokedAt;

    String refreshToken;
}
