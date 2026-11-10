package vn.tuhoc.InteractionService.domains.entities;

import java.util.List;

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
import vn.tuhoc.InteractionService.domains.enums.CommentStatusEnum;
import vn.tuhoc.InteractionService.domains.enums.CommentTargetTypeEnum;

@Document(collection = "comments")
@CompoundIndex(name = "idx_comments_target_created", def = "{'targetType': 1, 'targetId': 1, 'status': 1, 'createdAt': -1, '_id': -1}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentEntity {
    @Id
    Integer id;

    @Indexed
    Integer userId;

    Integer targetId;

    CommentTargetTypeEnum targetType;

    String createdAt;

    String updatedAt;

    String content;

    Integer likeCount;

    Integer replyCount;

    CommentStatusEnum status;

    List<Integer> hashtagIds;

    List<CommentMention> mentions;
}
