package vn.tuhoc.ContentService.dtos.clients;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileVisibilityResClientDTO {
    Integer userId;
    Boolean canViewPosts;
    Boolean canViewHighlights;
    Boolean canViewReposts;
}
