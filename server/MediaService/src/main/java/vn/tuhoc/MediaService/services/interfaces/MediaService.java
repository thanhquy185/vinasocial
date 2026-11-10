package vn.tuhoc.MediaService.services.interfaces;

import java.util.List;

import vn.tuhoc.MediaService.dtos.clients.MediaValidReqClientDTO;
import vn.tuhoc.MediaService.dtos.requests.MediaCreateRequestDTO;
import vn.tuhoc.MediaService.dtos.requests.MediasCreateRequestDTO;
import vn.tuhoc.MediaService.dtos.responses.MediaDetailResponseDTO;
import vn.tuhoc.core.dtos.clients.MediaInfoReqClientDTO;
import vn.tuhoc.core.dtos.clients.MediaInfoResClientDTO;

public interface MediaService {
        Boolean handleValidate(MediaValidReqClientDTO mediaValidReqClientDTO);

        List<MediaInfoResClientDTO> handleGetInfo(MediaInfoReqClientDTO mediaInfoReqClientDTO);

        MediaDetailResponseDTO handleGetById(String id);

        MediaDetailResponseDTO handleCreate(
                        Boolean isEncrypt,
                        Boolean isE2EE,
                        MediaCreateRequestDTO mediaCreateRequestDTO);

        List<MediaDetailResponseDTO> handleCreateMedias(
                        Boolean isEncrypt,
                        Boolean isE2EE,
                        MediasCreateRequestDTO mediasCreateRequestDTO);
}
