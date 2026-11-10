package vn.tuhoc.InteractionService.domains.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.enums.ShareTargetTypeEnum;
import vn.tuhoc.InteractionService.domains.enums.ShareTypeEnum;

@Document(collection = "shares")
@CompoundIndex(name = "idx_shares_target_created", def = "{'targetType': 1, 'targetId': 1, 'createdAt': -1, '_id': -1}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShareEntity {
    @Id
    Integer id;

    Integer userId;

    Integer targetId;

    ShareTargetTypeEnum targetType;

    String createdAt;

    ShareTypeEnum shareType;
}
