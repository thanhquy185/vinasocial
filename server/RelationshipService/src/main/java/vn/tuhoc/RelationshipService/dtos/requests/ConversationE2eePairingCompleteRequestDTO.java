package vn.tuhoc.RelationshipService.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationE2eePairingCompleteRequestDTO {
    @NotBlank
    @Pattern(regexp = "^[0-9]{8}$")
    String pairingCode;
}
