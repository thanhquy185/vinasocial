package vn.tuhoc.ContentService.services;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.entities.PostStatsEntity;
import vn.tuhoc.ContentService.domains.mappers.PostStatsMapper;
import vn.tuhoc.ContentService.dtos.requests.PostStatsCreateRequestDTO;
import vn.tuhoc.ContentService.dtos.responses.PostStatsDetailResponseDTO;
import vn.tuhoc.ContentService.exceptions.PostStatsNotFoundByPostIdException;
import vn.tuhoc.ContentService.repositories.PostStatsRepository;
import vn.tuhoc.ContentService.services.interfaces.PostStatsService;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostStatsServiceImplement implements PostStatsService {
    PostStatsRepository postStatsRepository;
    PostStatsMapper postStatsMapper;

    private PostStatsEntity getByPostId(Integer postId) {
        return this.postStatsRepository.findById(postId)
                .orElseThrow(() -> new PostStatsNotFoundByPostIdException(postId));
    }

    @Override
    public PostStatsDetailResponseDTO handleGetByPostId(Integer postId) {
        return this.postStatsMapper.entityToDetailResponse(this.getByPostId(postId));
    }

    @Override
    public PostStatsDetailResponseDTO handleCreate(
            PostStatsCreateRequestDTO postStatsCreateRequestDTO) {
        PostStatsEntity postStatsEntity = this.postStatsMapper
                .createEntityFromRequest(postStatsCreateRequestDTO);

        return this.postStatsMapper.entityToDetailResponse(
                this.postStatsRepository.save(postStatsEntity));
    }

}
