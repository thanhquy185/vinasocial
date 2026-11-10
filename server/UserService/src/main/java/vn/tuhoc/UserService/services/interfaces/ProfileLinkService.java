package vn.tuhoc.UserService.services.interfaces;

import vn.tuhoc.UserService.dtos.requests.ProfileLinkCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileLinkDetailResponseDTO;

public interface ProfileLinkService {
    ProfileLinkDetailResponseDTO handleGetByProfileId(Integer profileId);

    ProfileLinkDetailResponseDTO handleCreate(ProfileLinkCreateRequestDTO profileLinkCreateRequestDTO);
}
