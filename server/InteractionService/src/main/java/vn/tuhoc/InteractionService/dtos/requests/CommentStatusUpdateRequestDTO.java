package vn.tuhoc.InteractionService.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.enums.CommentStatusEnum;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentStatusUpdateRequestDTO {
    @NotNull
    CommentStatusEnum status;
}
