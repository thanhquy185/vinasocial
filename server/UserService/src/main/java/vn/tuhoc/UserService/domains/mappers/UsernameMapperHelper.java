package vn.tuhoc.UserService.domains.mappers;

import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.entities.UsernameEntity;
import vn.tuhoc.UserService.dtos.responses.UsernameDetailResponseDTO;
import vn.tuhoc.UserService.dtos.responses.UsernameInfoResponseDTO;
import vn.tuhoc.UserService.dtos.responses.UsernameSummaryResponseDTO;
import vn.tuhoc.UserService.repositories.UsernameRepository;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsernameMapperHelper {
    final UsernameRepository usernameRepository;
    final UsernameMapper usernameMapper;

    public UsernameEntity mapToEntity(Integer id) {
        return this.usernameRepository.findById(id).get();
    }

    public UsernameDetailResponseDTO mapToDetailResponse(UsernameEntity usernameEntity) {
        return this.usernameMapper.entityToDetailResponse(usernameEntity);
    }

    public UsernameSummaryResponseDTO mapToSummaryResponse(UsernameEntity usernameEntity) {
        return this.usernameMapper.entityToSummaryResponse(usernameEntity);
    }

    public UsernameInfoResponseDTO mapToInfoResponse(UsernameEntity usernameEntity) {
        return this.usernameMapper.entityToInfoResponse(usernameEntity);
    }
}
