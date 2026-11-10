package vn.tuhoc.InteractionService.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentMentionRequestDTO {
    @NotNull
    @Positive
    Integer userId;

    @NotNull
    @PositiveOrZero
    Integer startIndex;

    @NotNull
    @Positive
    Integer endIndex;
}
