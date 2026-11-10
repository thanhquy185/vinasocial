package vn.tuhoc.ContentService.dtos.responses;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.enums.PostTypeEnum;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfilePostResponseDTO {
    Integer id;
    String imageUrl;
    String caption;
    PostTypeEnum type;
    Integer likeCount;
    Integer commentCount;
    Integer viewCount;
    Boolean isPinned;
    Boolean isCarousel;
}
