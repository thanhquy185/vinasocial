package vn.tuhoc.RelationshipService.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationE2eeHistoryKeyWrapperItemRequestDTO {
    @NotNull
    @Positive
    Integer messageId;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9+/]{512}$")
    String wrappedKey;

    @NotBlank
    @Pattern(regexp = "^[a-f0-9]{64}$")
    String ciphertextHash;
}
