package vn.tuhoc.UserService.domains.mappers;

import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.entities.ProfileLinkEntity;
import vn.tuhoc.UserService.dtos.responses.ProfileLinkDetailResponseDTO;
import vn.tuhoc.UserService.repositories.ProfileLinkRepository;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileLinkMapperHelper {
    final ProfileLinkRepository profileLinkRepository;
    final ProfileLinkMapper profileLinkMapper;

    public ProfileLinkEntity mapToEntity(Integer id) {
        return this.profileLinkRepository.findById(id).get();
    }

    public ProfileLinkDetailResponseDTO mapToDetailResponse(ProfileLinkEntity profileLinkEntity) {
        return this.profileLinkMapper.entityToDetailResponse(profileLinkEntity);
    }
}
