package vn.tuhoc.ContentService.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.enums.StoryVisibilityEnum;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoryCreateRequestDTO {
    @NotBlank(message = "File phương tiện của tin không được để trống!")
    String mediaId;

    StoryVisibilityEnum visibility;

    Boolean allowLike;

    Boolean allowReply;

    Boolean allowShare;
}
