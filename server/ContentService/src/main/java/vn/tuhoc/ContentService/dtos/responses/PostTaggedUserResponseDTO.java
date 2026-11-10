package vn.tuhoc.ContentService.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.tuhoc.core.dtos.clients.UserInfoResClientDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostTaggedUserResponseDTO {
    Integer userId;
    UserInfoResClientDTO user;
}
