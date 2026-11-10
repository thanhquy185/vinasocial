package vn.tuhoc.ContentService.dtos.requests;

import jakarta.persistence.Convert;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.converters.StoryModerationStatusConverter;
import vn.tuhoc.ContentService.domains.enums.StoryModerationStatusEnum;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoryUpdateModerationStatusRequestDTO {
    @NotNull(message = "Trạng thái kiểm duyệt câu chuyện không được để trống!")
    @Convert(converter = StoryModerationStatusConverter.class)
    StoryModerationStatusEnum moderationStatus;
}
