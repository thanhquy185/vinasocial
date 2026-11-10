package vn.tuhoc.RelationshipService.dtos.requests;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupConversationCreateRequestDTO {
    @Size(max = 100)
    String name;

    @NotEmpty
    @Size(min = 2, max = 49)
    List<@NotBlank @Size(max = 100) String> usernames;
}
