package vn.tuhoc.ContentService.dtos.requests;

import java.util.List;

import jakarta.persistence.Convert;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.converters.PostSettingPrivacyConverter;
import vn.tuhoc.ContentService.domains.converters.PostTypeConverter;
import vn.tuhoc.ContentService.domains.enums.PostSettingPrivacyEnum;
import vn.tuhoc.ContentService.domains.enums.PostTypeEnum;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostCreateRequestDTO {
    @NotNull(message = "Loại bài đăng không được để trống!")
    @Convert(converter = PostTypeConverter.class)
    PostTypeEnum type;

    String caption;

    @NotNull(message = "Cài đặt quyền riêng tư không được để trống!")
    @Convert(converter = PostSettingPrivacyConverter.class)
    PostSettingPrivacyEnum postSettingPrivacy;

    @NotNull(message = "Cài đặt ghim bài đăng không được để trống!")
    Boolean postSettingIsPinned;

    @NotNull(message = "Cài đặt cho phép bình luận không được để trống!")
    Boolean postSettingAllowComment;

    @NotNull(message = "Cài đặt cho phép đăng lại không được để trống!")
    Boolean postSettingAllowReup;

    @NotNull(message = "Cài đặt cho phép chia sẻ không được để trống!")
    Boolean postSettingAllowShare;

    @NotNull(message = "Cài đặt cho phép lưu không được để trống!")
    Boolean postSettingAllowSave;

    @NotNull(message = "Cài đặt hiển thị số lượt xem không được để trống!")
    Boolean postSettingShowViewCount;

    @NotNull(message = "Cài đặt hiển thị số lượt thích không được để trống!")
    Boolean postSettingShowLikeCount;

    @NotNull(message = "Cài đặt hiển thị số lượt bình luận không được để trống!")
    Boolean postSettingShowCommentCount;

    @NotNull(message = "Cài đặt hiển thị số lượt đăng lại không được để trống!")
    Boolean postSettingShowReupCount;

    @NotNull(message = "Cài đặt hiển thị số lượt chia sẻ không được để trống!")
    Boolean postSettingShowShareCount;

    @NotNull(message = "Cài đặt hiển thị số lượt lưu không được để trống!")
    Boolean postSettingShowSaveCount;

    @NotNull(message = "File phương tiện của bài đăng không được để trống!")
    @Size(min = 1, message = "File phương tiện của bài đăng cần ít nhất là 1!")
    List<String> postMediaIds;

    @Size(max = 100, message = "Bài đăng chỉ có thể gắn thẻ tối đa 100 tài khoản.")
    List<Integer> taggedUserIds;
}
