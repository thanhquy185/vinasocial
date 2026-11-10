package vn.tuhoc.UserService.services.interfaces;

import vn.tuhoc.UserService.dtos.requests.ProfileStatsCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.ProfileStatsDetailResponseDTO;

public interface ProfileStatsService {
    ProfileStatsDetailResponseDTO handleGetByProfileId(Integer profileId);

    ProfileStatsDetailResponseDTO handleCreate(ProfileStatsCreateRequestDTO profileStatsCreateRequestDTO);
}
