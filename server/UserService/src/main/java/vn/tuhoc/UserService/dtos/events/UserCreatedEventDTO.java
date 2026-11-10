package vn.tuhoc.UserService.dtos.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserCreatedEventDTO {
    private String fullname;

    private String username;
}
