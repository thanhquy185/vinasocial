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
public class TargetStatsResponseDTO {
    Integer targetId;
    String targetType;
    long viewCount;
    long likeCount;
    long commentCount;
    long shareCount;
    long reupCount;
    boolean likedByCurrentUser;
    boolean savedByCurrentUser;
    boolean reupByCurrentUser;
}
