package vn.tuhoc.InteractionService.dtos.requests;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.enums.CommentTargetTypeEnum;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentCreateRequestDTO {
    @NotNull
    CommentTargetTypeEnum targetType;

    @NotNull
    @Positive
    Integer targetId;

    @NotBlank
    @Size(max = 5000)
    String content;

    List<@Positive Integer> hashtagIds;

    @Valid
    List<CommentMentionRequestDTO> mentions;
}
