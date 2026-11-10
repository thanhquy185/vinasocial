package vn.tuhoc.ContentService.dtos.requests;

import jakarta.validation.constraints.NotNull;
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
public class StorySettingUpdateRequestDTO {
    @NotNull(message = "Quyền xem tin không được để trống!")
    StoryVisibilityEnum visibility;

    @NotNull(message = "Cài đặt lượt thích không được để trống!")
    Boolean allowLike;

    @NotNull(message = "Cài đặt trả lời không được để trống!")
    Boolean allowReply;

    @NotNull(message = "Cài đặt chia sẻ không được để trống!")
    Boolean allowShare;
}
