package vn.tuhoc.RelationshipService.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.RelationshipService.domains.enums.ConversationParticipantRoleEnum;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationParticipantRoleUpdateRequestDTO {
    @NotNull
    ConversationParticipantRoleEnum role;
}
