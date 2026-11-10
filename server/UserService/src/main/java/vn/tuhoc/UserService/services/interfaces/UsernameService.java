package vn.tuhoc.UserService.services.interfaces;

import vn.tuhoc.UserService.dtos.requests.UsernameCreateRequestDTO;
import vn.tuhoc.UserService.dtos.responses.UsernameDetailResponseDTO;

public interface UsernameService {
    void handleExistsByValue(String value);

    UsernameDetailResponseDTO handleCreate(UsernameCreateRequestDTO usernameCreateRequestDTO);
}
