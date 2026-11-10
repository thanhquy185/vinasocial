package vn.tuhoc.core.dtos.clients;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoResClientDTO {
    Integer id;

    String publicId;

    @JsonIgnore
    String avatarMediaId;

    MediaInfoResClientDTO avatar;

    String username;

    String alias;

    String role;

    Boolean canViewPosts;

    public UserInfoResClientDTO(Integer id, String publicId, String avatarMediaId, String username, String alias) {
        this.id = id;
        this.publicId = publicId;
        this.avatarMediaId = avatarMediaId;
        this.username = username;
        this.alias = alias;
    }

    public UserInfoResClientDTO(
            Integer id,
            String publicId,
            String avatarMediaId,
            String username,
            String alias,
            Object role) {
        this(id, publicId, avatarMediaId, username, alias);
        this.role = role instanceof Enum<?> enumRole
                ? enumRole.name()
                : role == null ? null : role.toString();
    }

    public UserInfoResClientDTO(
            Integer id,
            String publicId,
            String avatarMediaId,
            String username,
            String alias,
            Object role,
            Boolean canViewPosts) {
        this(id, publicId, avatarMediaId, username, alias, role);
        this.canViewPosts = canViewPosts;
    }
}
