package vn.tuhoc.ContentService.services;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.entities.PostSettingEntity;
import vn.tuhoc.ContentService.domains.mappers.PostSettingMapper;
import vn.tuhoc.ContentService.dtos.requests.PostSettingCreateRequestDTO;
import vn.tuhoc.ContentService.dtos.responses.PostSettingDetailResponseDTO;
import vn.tuhoc.ContentService.exceptions.PostSettingNotFoundByPostIdException;
import vn.tuhoc.ContentService.repositories.PostSettingRepository;
import vn.tuhoc.ContentService.services.interfaces.PostSettingService;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostSettingServiceImplement implements PostSettingService {
    PostSettingRepository postSettingRepository;
    PostSettingMapper postSettingMapper;

    private PostSettingEntity getByPostId(Integer postId) {
        return this.postSettingRepository.findById(postId)
                .orElseThrow(() -> new PostSettingNotFoundByPostIdException(postId));
    }

    @Override
    public PostSettingDetailResponseDTO handleGetByPostId(Integer postId) {
        return this.postSettingMapper.entityToDetailResponse(this.getByPostId(postId));
    }

    @Override
    public PostSettingDetailResponseDTO handleCreate(
            PostSettingCreateRequestDTO postSettingCreateRequestDTO) {
        PostSettingEntity postSettingEntity = this.postSettingMapper
                .createEntityFromRequest(postSettingCreateRequestDTO);

        return this.postSettingMapper.entityToDetailResponse(
                this.postSettingRepository.save(postSettingEntity));
    }

}
