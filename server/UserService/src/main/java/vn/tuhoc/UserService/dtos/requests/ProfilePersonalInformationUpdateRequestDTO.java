package vn.tuhoc.UserService.dtos.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.enums.ProfileGenderEnum;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfilePersonalInformationUpdateRequestDTO {
    String firstName;
    String lastName;
    String birthdate;
    ProfileGenderEnum gender;
    String phone;
    String email;
}
