package vn.tuhoc.InteractionService.dtos.responses;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportDetailResponseDTO {
    Integer id;
    Integer userId;
    Integer targetId;
    String targetType;
    String createdAt;
    String reason;
    String status;
}
