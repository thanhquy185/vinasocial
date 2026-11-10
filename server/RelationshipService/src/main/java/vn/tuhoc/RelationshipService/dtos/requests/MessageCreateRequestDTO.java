package vn.tuhoc.RelationshipService.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.RelationshipService.domains.enums.MessageTypeEnum;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageCreateRequestDTO {
    @NotBlank
    @Size(max = 36)
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-8][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")
    String clientMessageId;

    @NotNull
    MessageTypeEnum type;

    @NotBlank
    @Size(max = 2_000_000)
    String ciphertext;

    Integer replyToMessageId;

}
