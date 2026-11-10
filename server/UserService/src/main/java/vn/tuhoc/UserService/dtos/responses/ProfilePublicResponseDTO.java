package vn.tuhoc.UserService.dtos.responses;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.core.dtos.clients.MediaInfoResClientDTO;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfilePublicResponseDTO {
    Integer userId;
    String username;
    String firstName;
    String lastName;
    String alias;
    String bio;
    MediaInfoResClientDTO avatar;
    ProfileStatsDetailResponseDTO stats;
    List<ProfileLinkDetailResponseDTO> links;
    Boolean isVerified;
    Boolean isOwnProfile;
    Boolean isPrivate;
    Boolean canViewProfile;
    Boolean canViewPosts;
    Boolean canViewReposts;
    Boolean canViewHighlights;
}
