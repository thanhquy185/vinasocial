package vn.tuhoc.UserService.domains.mappers;

import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.entities.ProfileStatsEntity;
import vn.tuhoc.UserService.dtos.responses.ProfileStatsDetailResponseDTO;
import vn.tuhoc.UserService.repositories.ProfileStatsRepository;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileStatsMapperHelper {
    final ProfileStatsRepository profileStatsRepository;
    final ProfileStatsMapper profileStatsMapper;

    public ProfileStatsEntity mapToEntity(Integer id) {
        return this.profileStatsRepository.findById(id).get();
    }

    public ProfileStatsDetailResponseDTO mapToDetailResponse(ProfileStatsEntity profileStatsEntity) {
        return this.profileStatsMapper.entityToDetailResponse(profileStatsEntity);
    }
}
