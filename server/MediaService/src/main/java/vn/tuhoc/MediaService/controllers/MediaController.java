package vn.tuhoc.MediaService.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.MediaService.dtos.clients.MediaValidReqClientDTO;
import vn.tuhoc.MediaService.dtos.requests.MediaCreateRequestDTO;
import vn.tuhoc.MediaService.dtos.requests.MediasCreateRequestDTO;
import vn.tuhoc.MediaService.dtos.responses.MediaDetailResponseDTO;
import vn.tuhoc.MediaService.services.interfaces.MediaService;
import vn.tuhoc.core.dtos.clients.MediaInfoReqClientDTO;
import vn.tuhoc.core.dtos.clients.MediaInfoResClientDTO;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ModelAttribute;

@RestController
@RequestMapping("/api/v1/medias")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MediaController {
        MediaService mediaService;

        @PostMapping(value = "/validate", consumes = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<Boolean> validateMedia(@RequestBody MediaValidReqClientDTO mediaValidReqClientDTO) {
                return ResponseEntity.ok(this.mediaService.handleValidate(mediaValidReqClientDTO));
        }

        @PostMapping(value = "/info", consumes = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<List<MediaInfoResClientDTO>> getInfo(
                        @RequestBody MediaInfoReqClientDTO mediaInfoReqClientDTO) {
                return ResponseEntity.ok(this.mediaService.handleGetInfo(mediaInfoReqClientDTO));
        }

        @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<RestResponseDTO<MediaDetailResponseDTO>> create(
                        @RequestParam(name = "isEncrypt", required = true) Boolean isEncrypt,
                        @RequestParam(name = "isE2EE", required = true) Boolean isE2EE,
                        @ModelAttribute @Valid MediaCreateRequestDTO mediaCreateRequestDTO) {
                MediaDetailResponseDTO mediaCreated = this.mediaService.handleCreate(
                                isEncrypt,
                                isE2EE,
                                mediaCreateRequestDTO);

                return RestResponseUtil.created(
                                "Thêm phương tiện thành công!",
                                mediaCreated);
        }

        @PostMapping(value = "/is-medias", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<RestResponseDTO<List<MediaDetailResponseDTO>>> createMedias(
                        @RequestParam(name = "isEncrypt", required = true) Boolean isEncrypt,
                        @RequestParam(name = "isE2EE", required = true) Boolean isE2EE,
                        @ModelAttribute @Valid MediasCreateRequestDTO mediasCreateRequestDTO) {
                List<MediaDetailResponseDTO> mediasCreated = this.mediaService.handleCreateMedias(
                                isEncrypt,
                                isE2EE,
                                mediasCreateRequestDTO);

                return RestResponseUtil.created(
                                "Thêm nhiều phương tiện thành công!",
                                mediasCreated);
        }

}
