package vn.tuhoc.ContentService.domains.mappers;

import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.entities.PostSettingEntity;
import vn.tuhoc.ContentService.dtos.responses.PostSettingDetailResponseDTO;
import vn.tuhoc.ContentService.repositories.PostSettingRepository;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostSettingMapperHelper {
    final PostSettingRepository postSettingRepository;
    final PostSettingMapper postSettingMapper;

    public PostSettingEntity mapToEntity(Integer id) {
        return this.postSettingRepository.findById(id).get();
    }

    public PostSettingDetailResponseDTO mapToDetailResponse(PostSettingEntity postSettingEntity) {
        return this.postSettingMapper.entityToDetailResponse(postSettingEntity);
    }
}
