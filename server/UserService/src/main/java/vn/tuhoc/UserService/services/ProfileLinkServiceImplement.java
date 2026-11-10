package vn.tuhoc.UserService.services;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.entities.ProfileLinkEntity;
import vn.tuhoc.UserService.domains.mappers.ProfileLinkMapper;
import vn.tuhoc.UserService.dtos.requests.ProfileLinkCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileLinkDetailResponseDTO;
import vn.tuhoc.UserService.exceptions.ProfileLinkNotFoundByProfileIdException;
import vn.tuhoc.UserService.repositories.ProfileLinkRepository;
import vn.tuhoc.UserService.services.interfaces.ProfileLinkService;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileLinkServiceImplement implements ProfileLinkService {
    ProfileLinkRepository profileLinkRepository;
    ProfileLinkMapper profileLinkMapper;

    private ProfileLinkEntity getByProfileId(Integer profileId) {
        return this.profileLinkRepository.findById(profileId)
                .orElseThrow(() -> new ProfileLinkNotFoundByProfileIdException(profileId));
    }

    @Override
    public ProfileLinkDetailResponseDTO handleGetByProfileId(Integer profileId) {
        return this.profileLinkMapper.entityToDetailResponse(this.getByProfileId(profileId));
    }

    @Override
    public ProfileLinkDetailResponseDTO handleCreate(ProfileLinkCreateRequestDTO profileLinkCreateRequestDTO) {
        ProfileLinkEntity profileLinkEntity = this.profileLinkMapper
                .createEntityFromRequest(profileLinkCreateRequestDTO);

        return this.profileLinkMapper.entityToDetailResponse(this.profileLinkRepository.save(profileLinkEntity));
    }

}
