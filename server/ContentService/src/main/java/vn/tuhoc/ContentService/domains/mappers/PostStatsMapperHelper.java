package vn.tuhoc.ContentService.domains.mappers;

import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.entities.PostStatsEntity;
import vn.tuhoc.ContentService.dtos.responses.PostStatsDetailResponseDTO;
import vn.tuhoc.ContentService.repositories.PostStatsRepository;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostStatsMapperHelper {
    final PostStatsRepository postStatsRepository;
    final PostStatsMapper postStatsMapper;

    public PostStatsEntity mapToEntity(Integer id) {
        return this.postStatsRepository.findById(id).get();
    }

    public PostStatsDetailResponseDTO mapToDetailResponse(PostStatsEntity postStatsEntity) {
        return this.postStatsMapper.entityToDetailResponse(postStatsEntity);
    }
}
