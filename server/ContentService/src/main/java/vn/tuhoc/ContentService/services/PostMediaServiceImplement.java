package vn.tuhoc.ContentService.services;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.entities.PostMediaEntity;
import vn.tuhoc.ContentService.domains.mappers.PostMediaMapper;
import vn.tuhoc.ContentService.dtos.requests.PostMediaCreateRequestDTO;
import vn.tuhoc.ContentService.dtos.responses.PostMediaDetailResponseDTO;
import vn.tuhoc.ContentService.exceptions.PostMediaNotFoundByIdException;
import vn.tuhoc.ContentService.exceptions.PostMediaNotFoundByMediaIdException;
import vn.tuhoc.ContentService.exceptions.PostMediaNotFoundByPostIdException;
import vn.tuhoc.ContentService.repositories.PostMediaRepository;
import vn.tuhoc.ContentService.services.interfaces.PostMediaService;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostMediaServiceImplement implements PostMediaService {
    PostMediaRepository postMediaRepository;
    PostMediaMapper postMediaMapper;

    private PostMediaEntity getById(Integer id) {
        return this.postMediaRepository.findById(id)
                .orElseThrow(() -> new PostMediaNotFoundByIdException(id));
    }

    private PostMediaEntity getByPostId(Integer postId) {
        return this.postMediaRepository.findByPostId(postId)
                .orElseThrow(() -> new PostMediaNotFoundByPostIdException(postId));
    }

    private PostMediaEntity getByMediaId(String mediaId) {
        return this.postMediaRepository.findByMediaId(mediaId)
                .orElseThrow(() -> new PostMediaNotFoundByMediaIdException(mediaId));
    }

    @Override
    public PostMediaDetailResponseDTO handleGetById(Integer id) {
        return this.postMediaMapper.entityToDetailResponse(this.getById(id));
    }

    @Override
    public PostMediaDetailResponseDTO handleGetByPostId(Integer postId) {
        return this.postMediaMapper.entityToDetailResponse(this.getByPostId(postId));
    }

    @Override
    public PostMediaDetailResponseDTO handleGetByMediaId(String mediaId) {
        return this.postMediaMapper.entityToDetailResponse(this.getByMediaId(mediaId));
    }

    @Override
    public PostMediaDetailResponseDTO handleCreate(PostMediaCreateRequestDTO postMediaCreateRequestDTO) {
        PostMediaEntity postMediaEntity = this.postMediaMapper.createEntityFromRequest(postMediaCreateRequestDTO);

        return this.postMediaMapper.entityToDetailResponse(this.postMediaRepository.save(postMediaEntity));
    }

    @Override
    public List<PostMediaDetailResponseDTO> handleCreateAll(
            List<PostMediaCreateRequestDTO> postMediaCreateRequestDTOs) {

        List<PostMediaEntity> postMediaEntities = postMediaCreateRequestDTOs.stream()
                .map(postMediaCreateRequestDTO -> this.postMediaMapper
                        .createEntityFromRequest(postMediaCreateRequestDTO))
                .toList();

        List<PostMediaEntity> postMediaEntitiesCreated = this.postMediaRepository.saveAll(postMediaEntities);

        return postMediaEntitiesCreated.stream()
                .map(this.postMediaMapper::entityToDetailResponse)
                .toList();
    }
}
