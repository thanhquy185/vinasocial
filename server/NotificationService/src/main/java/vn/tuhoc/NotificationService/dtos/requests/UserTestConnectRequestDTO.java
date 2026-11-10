package vn.tuhoc.NotificationService.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserTestConnectRequestDTO {
    private String username;

    private String fullname;
}
