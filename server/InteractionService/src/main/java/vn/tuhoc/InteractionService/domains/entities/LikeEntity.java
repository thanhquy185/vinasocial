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
import vn.tuhoc.InteractionService.domains.enums.LikeTargetTypeEnum;

@Document(collection = "likes")
@CompoundIndex(name = "uk_likes_user_target", def = "{'userId': 1, 'targetType': 1, 'targetId': 1}", unique = true)
@CompoundIndex(name = "idx_likes_target_created", def = "{'targetType': 1, 'targetId': 1, 'createdAt': -1, '_id': -1}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LikeEntity {
    @Id
    Integer id;

    @Indexed
    Integer userId;

    Integer targetId;

    LikeTargetTypeEnum targetType;

    String createdAt;
}
