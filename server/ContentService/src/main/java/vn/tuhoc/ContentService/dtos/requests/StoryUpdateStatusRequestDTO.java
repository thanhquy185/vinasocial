package vn.tuhoc.ContentService.dtos.requests;

import jakarta.persistence.Convert;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.converters.StoryStatusConverter;
import vn.tuhoc.ContentService.domains.enums.StoryStatusEnum;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoryUpdateStatusRequestDTO {
    @NotNull(message = "Trạng thái câu chuyện không được để trống!")
    @Convert(converter = StoryStatusConverter.class)
    StoryStatusEnum status;
}
