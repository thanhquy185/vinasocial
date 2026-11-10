package vn.tuhoc.InteractionService.dtos.clients;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileVisibilityResponseDTO {
    private Integer userId;
    private Boolean canViewReposts;
}
