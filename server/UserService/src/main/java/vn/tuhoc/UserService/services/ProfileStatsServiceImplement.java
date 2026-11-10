package vn.tuhoc.UserService.services;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.entities.ProfileStatsEntity;
import vn.tuhoc.UserService.domains.mappers.ProfileStatsMapper;
import vn.tuhoc.UserService.dtos.requests.ProfileStatsCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileStatsDetailResponseDTO;
import vn.tuhoc.UserService.exceptions.ProfileStatsNotFoundByProfileIdException;
import vn.tuhoc.UserService.repositories.ProfileStatsRepository;
import vn.tuhoc.UserService.services.interfaces.ProfileStatsService;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileStatsServiceImplement implements ProfileStatsService {
    ProfileStatsRepository profileStatsRepository;
    ProfileStatsMapper profileStatsMapper;

    private ProfileStatsEntity getByProfileId(Integer profileId) {
        return this.profileStatsRepository.findById(profileId)
                .orElseThrow(() -> new ProfileStatsNotFoundByProfileIdException(profileId));
    }

    @Override
    public ProfileStatsDetailResponseDTO handleGetByProfileId(Integer profileId) {
        return this.profileStatsMapper.entityToDetailResponse(this.getByProfileId(profileId));
    }

    @Override
    public ProfileStatsDetailResponseDTO handleCreate(ProfileStatsCreateRequestDTO profileStatsCreateRequestDTO) {
        ProfileStatsEntity profileStatsEntity = this.profileStatsMapper
                .createEntityFromRequest(profileStatsCreateRequestDTO);

        return this.profileStatsMapper.entityToDetailResponse(this.profileStatsRepository.save(profileStatsEntity));
    }

}
