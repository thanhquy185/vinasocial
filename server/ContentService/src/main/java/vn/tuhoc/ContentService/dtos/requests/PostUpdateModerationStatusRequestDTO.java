package vn.tuhoc.ContentService.dtos.requests;

import jakarta.persistence.Convert;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.converters.PostModerationStatusConverter;
import vn.tuhoc.ContentService.domains.enums.PostModerationStatusEnum;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostUpdateModerationStatusRequestDTO {
    @NotNull(message = "Trạng thái kiểm duyệt bài đăng không được để trống!")
    @Convert(converter = PostModerationStatusConverter.class)
    PostModerationStatusEnum moderationStatus;
}
