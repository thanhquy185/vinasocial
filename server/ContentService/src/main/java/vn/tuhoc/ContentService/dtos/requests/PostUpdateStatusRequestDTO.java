package vn.tuhoc.ContentService.dtos.requests;

import jakarta.persistence.Convert;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.converters.PostStatusConverter;
import vn.tuhoc.ContentService.domains.enums.PostStatusEnum;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostUpdateStatusRequestDTO {
    @NotNull(message = "Trạng thái bài đăng không được để trống!")
    @Convert(converter = PostStatusConverter.class)
    PostStatusEnum status;
}
