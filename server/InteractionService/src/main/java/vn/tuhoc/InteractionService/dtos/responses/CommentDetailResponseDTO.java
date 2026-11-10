package vn.tuhoc.InteractionService.dtos.responses;

import java.util.List;

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
public class CommentDetailResponseDTO {
    Integer id;
    Integer userId;
    Integer targetId;
    String targetType;
    String createdAt;
    String updatedAt;
    String content;
    Integer likeCount;
    Integer replyCount;
    String status;
    List<Integer> hashtagIds;
    List<CommentMentionSummaryResponseDTO> mentions;
}
