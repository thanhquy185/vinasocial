package vn.tuhoc.ContentService.dtos.responses;

import java.util.List;

import jakarta.persistence.Convert;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.converters.PostModerationStatusConverter;
import vn.tuhoc.ContentService.domains.converters.PostStatusConverter;
import vn.tuhoc.ContentService.domains.converters.PostTypeConverter;
import vn.tuhoc.ContentService.domains.enums.PostModerationStatusEnum;
import vn.tuhoc.ContentService.domains.enums.PostStatusEnum;
import vn.tuhoc.ContentService.domains.enums.PostTypeEnum;
import vn.tuhoc.core.dtos.clients.UserInfoResClientDTO;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostDetailResponseDTO {
    Integer id;

    String publicId;

    UserInfoResClientDTO user;

    String createdAt;

    @Convert(converter = PostTypeConverter.class)
    PostTypeEnum type;

    String caption;

    @Convert(converter = PostModerationStatusConverter.class)
    PostModerationStatusEnum moderationStatus;

    @Convert(converter = PostStatusConverter.class)
    PostStatusEnum status;

    PostStatsDetailResponseDTO stats;

    PostSettingDetailResponseDTO setting;

    List<PostMediaDetailResponseDTO> medias;

    List<PostTaggedUserResponseDTO> taggedUsers;
}
