package vn.tuhoc.MediaService.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

import vn.tuhoc.MediaService.domains.enums.MediaTypeEnum;
import vn.tuhoc.MediaService.dtos.responses.CloudinaryUploadResponseDTO;

public interface CloudinaryService {
    CloudinaryUploadResponseDTO upload(MultipartFile file, MediaTypeEnum mediaTypeEnum);

    void delete(String publicId, MediaTypeEnum mediaTypeEnum);
}
