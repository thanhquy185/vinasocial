package vn.tuhoc.ContentService.dtos.responses;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostStatsDetailResponseDTO {
    Integer viewCount;

    Integer likeCount;

    Integer commentCount;

    Integer reupCount;

    Integer shareCount;

    Integer saveCount;
}
