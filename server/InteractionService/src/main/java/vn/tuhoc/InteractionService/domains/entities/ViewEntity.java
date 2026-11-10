package vn.tuhoc.InteractionService.domains.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.enums.ViewTargetTypeEnum;

@Document(collection = "views")
@CompoundIndexes({
        @CompoundIndex(name = "idx_views_target_created", def = "{'targetType': 1, 'targetId': 1, 'createdAt': -1, '_id': -1}"),
        @CompoundIndex(name = "idx_views_user_target", def = "{'userId': 1, 'targetType': 1, 'targetId': 1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewEntity {
    @Id
    Integer id;

    @Indexed
    Integer userId;

    Integer targetId;

    ViewTargetTypeEnum targetType;

    String createdAt;
}
