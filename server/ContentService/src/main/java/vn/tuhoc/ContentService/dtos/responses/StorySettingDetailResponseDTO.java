package vn.tuhoc.ContentService.dtos.responses;

import jakarta.persistence.Convert;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.converters.StoryVisibilityConverter;
import vn.tuhoc.ContentService.domains.enums.StoryVisibilityEnum;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StorySettingDetailResponseDTO {
    @Convert(converter = StoryVisibilityConverter.class)
    StoryVisibilityEnum visibility;

    Boolean allowLike;

    Boolean allowReply;

    Boolean allowShare;
}
