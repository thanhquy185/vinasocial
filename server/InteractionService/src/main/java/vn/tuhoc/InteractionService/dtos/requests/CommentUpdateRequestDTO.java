package vn.tuhoc.InteractionService.dtos.requests;

import java.util.List;

import jakarta.validation.Valid;
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
public class CommentUpdateRequestDTO {
    @NotBlank
    @Size(max = 5000)
    String content;

    List<Integer> hashtagIds;

    @Valid
    List<CommentMentionRequestDTO> mentions;
}
