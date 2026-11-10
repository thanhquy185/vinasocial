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
public class PostCriteria {
    Integer page;

    Integer size;

    Optional<String> id;

    Optional<String> publicId;

    Optional<String> userId;

    Optional<String> username;

    Optional<String> caption;

    Optional<String> createdAtStart;

    Optional<String> createdAtEnd;

    Optional<String> type;

    Optional<String> moderationStatus;

    Optional<String> status;

    Optional<String> sort;
}
