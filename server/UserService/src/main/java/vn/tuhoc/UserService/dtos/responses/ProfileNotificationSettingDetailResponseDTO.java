package vn.tuhoc.UserService.dtos.responses;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileNotificationSettingDetailResponseDTO {
    Boolean postEnabled;

    Boolean storyEnabled;

    Boolean collaboratorEnabled;

    Boolean tagEnabled;

    Boolean messageEnabled;

    Boolean followEnabled;

    Boolean friendEnabled;

    Boolean bestFriendEnabled;

    Boolean pageFollowingEnabled;

    Boolean replyCommentEnabled;
}
