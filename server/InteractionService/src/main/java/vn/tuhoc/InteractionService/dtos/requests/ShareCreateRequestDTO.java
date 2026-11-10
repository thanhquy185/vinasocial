package vn.tuhoc.InteractionService.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.enums.ShareTargetTypeEnum;
import vn.tuhoc.InteractionService.domains.enums.ShareTypeEnum;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShareCreateRequestDTO {
    @NotNull
    ShareTargetTypeEnum targetType;

    @NotNull
    @Positive
    Integer targetId;

    @NotNull
    ShareTypeEnum type;
}
