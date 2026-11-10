package vn.tuhoc.ContentService.domains.mappers;

import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.entities.PostEntity;
import vn.tuhoc.ContentService.repositories.PostRepository;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostMapperHelper {
    final PostRepository postRepository;

    public PostEntity mapToEntity(Integer id) {
        return this.postRepository.findById(id).get();
    }
}
