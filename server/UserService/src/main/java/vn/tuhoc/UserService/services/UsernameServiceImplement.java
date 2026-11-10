package vn.tuhoc.UserService.services;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.entities.UsernameEntity;
import vn.tuhoc.UserService.domains.mappers.UsernameMapper;
import vn.tuhoc.UserService.dtos.requests.UsernameCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.UsernameDetailResponseDTO;
// import vn.tuhoc.UserService.exceptions.UsernameNotFoundByIdException;
import vn.tuhoc.UserService.exceptions.UsernameValueIsExistsException;
import vn.tuhoc.UserService.repositories.UsernameRepository;
import vn.tuhoc.UserService.services.interfaces.UsernameService;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UsernameServiceImplement implements UsernameService {
    UsernameRepository usernameRepository;
    UsernameMapper usernameMapper;

    // private UsernameEntity getById(Integer id) {
    // return this.usernameRepository.findById(id)
    // .orElseThrow(() -> new UsernameNotFoundByIdException(id));
    // }

    @Override
    public void handleExistsByValue(String value) {
        if (this.usernameRepository.existsByValue(value)) {
            throw new UsernameValueIsExistsException(value);
        }
    }

    @Override
    public UsernameDetailResponseDTO handleCreate(UsernameCreateRequestDTO usernameCreateRequestDTO) {
        UsernameEntity usernameEntity = this.usernameMapper.createEntityFromRequest(usernameCreateRequestDTO);

        return this.usernameMapper.entityToDetailResponse(this.usernameRepository.save(usernameEntity));
    }
}
