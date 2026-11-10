package vn.tuhoc.UserService.dtos.requests;

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
public class ProfileUpdateRequestDTO {
    String username;
    String alias;
    String bio;
    String website;
    String avatarId;
}
