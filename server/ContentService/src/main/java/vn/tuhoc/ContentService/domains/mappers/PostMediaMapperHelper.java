package vn.tuhoc.ContentService.domains.mappers;

import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.entities.PostMediaEntity;
import vn.tuhoc.ContentService.dtos.responses.PostMediaDetailResponseDTO;
import vn.tuhoc.ContentService.repositories.PostMediaRepository;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostMediaMapperHelper {
    final PostMediaRepository postMediaRepository;
    final PostMediaMapper postMediaMapper;

    public PostMediaEntity mapToEntity(Integer id) {
        return this.postMediaRepository.findById(id).get();
    }

    public PostMediaDetailResponseDTO mapToDetailResponse(PostMediaEntity postMediaEntity) {
        return this.postMediaMapper.entityToDetailResponse(postMediaEntity);
    }
}
