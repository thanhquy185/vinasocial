package vn.tuhoc.InteractionService.domains.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.enums.ReportStatusEnum;
import vn.tuhoc.InteractionService.domains.enums.ReportTargetTypeEnum;

@Document(collection = "reports")
@CompoundIndex(name = "idx_reports_status_created", def = "{'status': 1, 'createdAt': -1, '_id': -1}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportEntity {
    @Id
    Integer id;

    @Indexed
    Integer userId;

    @Indexed
    Integer targetId;

    ReportTargetTypeEnum targetType;

    String createdAt;

    String reason;

    ReportStatusEnum status;
}
