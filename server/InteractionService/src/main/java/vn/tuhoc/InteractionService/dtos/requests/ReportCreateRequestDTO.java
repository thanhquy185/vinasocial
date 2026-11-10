package vn.tuhoc.InteractionService.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.enums.ReportTargetTypeEnum;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportCreateRequestDTO {
    @NotNull
    ReportTargetTypeEnum targetType;

    @NotNull
    @Positive
    Integer targetId;

    @Size(max = 5000)
    String reason;
}
