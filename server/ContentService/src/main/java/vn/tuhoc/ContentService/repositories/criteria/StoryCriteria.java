package vn.tuhoc.ContentService.repositories.criteria;

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
public class StoryCriteria {
    Integer page;

    Integer size;

    Optional<String> id;

    Optional<String> publicId;

    Optional<String> userId;

    Optional<String> username;

    Optional<String> createdAtStart;

    Optional<String> createdAtEnd;

    Optional<String> expiredAtStart;

    Optional<String> expiredAtEnd;

    Optional<String> status;

    Optional<String> moderationStatus;

    Optional<String> visibility;

    Optional<String> sort;
}
