package vn.tuhoc.ContentService.dtos.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostStatsCreateRequestDTO {
    Integer postId;

    Integer viewCount;

    Integer likeCount;

    Integer commentCount;

    Integer reupCount;

    Integer shareCount;

    Integer saveCount;
}
