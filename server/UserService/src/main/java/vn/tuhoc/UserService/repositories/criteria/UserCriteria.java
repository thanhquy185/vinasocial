package vn.tuhoc.UserService.repositories.criteria;

import java.util.Optional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCriteria {
    Integer page;

    Integer size;

    Optional<String> id;

    Optional<String> publicId;

    Optional<String> createdAtStart;

    Optional<String> createdAtEnd;

    Optional<String> lastLoginAtStart;

    Optional<String> lastLoginAtEnd;

    Optional<String> username;

    Optional<String> role;

    Optional<String> status;

    Optional<String> sort;
}
