package vn.tuhoc.ContentService.dtos.responses;

import jakarta.persistence.Convert;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.converters.StoryModerationStatusConverter;
import vn.tuhoc.ContentService.domains.converters.StoryStatusConverter;
import vn.tuhoc.ContentService.domains.enums.StoryModerationStatusEnum;
import vn.tuhoc.ContentService.domains.enums.StoryStatusEnum;
import vn.tuhoc.core.dtos.clients.MediaInfoResClientDTO;
import vn.tuhoc.core.dtos.clients.UserInfoResClientDTO;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoryDetailResponseDTO {
    Integer id;

    String publicId;

    UserInfoResClientDTO user;

    String createdAt;

    String expiredAt;

    @Convert(converter = StoryStatusConverter.class)
    StoryStatusEnum status;

    @Convert(converter = StoryModerationStatusConverter.class)
    StoryModerationStatusEnum moderationStatus;

    MediaInfoResClientDTO media;

    StoryStatsDetailResponseDTO stats;

    StorySettingDetailResponseDTO setting;
}
