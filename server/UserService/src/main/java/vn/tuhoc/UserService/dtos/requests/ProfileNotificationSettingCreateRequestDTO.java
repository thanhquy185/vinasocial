package vn.tuhoc.UserService.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileNotificationSettingCreateRequestDTO {
    @NotNull(message = "Mã thông tin không được để trống!")
    Integer profileId;

    @NotNull(message = "Thiết lập thông báo bài viết không được để trống!")
    Boolean postEnabled;

    @NotNull(message = "Thiết lập thông báo tin không được để trống!")
    Boolean storyEnabled;

    @NotNull(message = "Thiết lập thông báo cộng tác viên không được để trống!")
    Boolean collaboratorEnabled;

    @NotNull(message = "Thiết lập thông báo gắn thẻ không được để trống!")
    Boolean tagEnabled;

    @NotNull(message = "Thiết lập thông báo tin nhắn không được để trống!")
    Boolean messageEnabled;

    @NotNull(message = "Thiết lập thông báo theo dõi không được để trống!")
    Boolean followEnabled;

    @NotNull(message = "Thiết lập thông báo kết bạn không được để trống!")
    Boolean friendEnabled;

    @NotNull(message = "Thiết lập thông báo bạn thân không được để trống!")
    Boolean bestFriendEnabled;

    @NotNull(message = "Thiết lập thông báo theo dõi trang không được để trống!")
    Boolean pageFollowingEnabled;

    @NotNull(message = "Thiết lập thông báo trả lời bình luận không được để trống!")
    Boolean replyCommentEnabled;
}
