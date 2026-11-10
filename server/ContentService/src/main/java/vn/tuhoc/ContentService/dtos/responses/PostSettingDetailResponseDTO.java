package vn.tuhoc.ContentService.dtos.responses;

import jakarta.persistence.Convert;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.converters.PostSettingPrivacyConverter;
import vn.tuhoc.ContentService.domains.enums.PostSettingPrivacyEnum;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostSettingDetailResponseDTO {
    @Convert(converter = PostSettingPrivacyConverter.class)
    PostSettingPrivacyEnum privacy;

    Boolean isPinned;

    Boolean allowComment;

    Boolean allowReup;

    Boolean allowShare;

    Boolean allowSave;

    Boolean showViewCount;

    Boolean showLikeCount;

    Boolean showCommentCount;

    Boolean showReupCount;

    Boolean showShareCount;

    Boolean showSaveCount;
}
