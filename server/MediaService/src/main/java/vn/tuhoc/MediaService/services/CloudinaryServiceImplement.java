package vn.tuhoc.MediaService.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.MediaService.domains.enums.MediaTypeEnum;
import vn.tuhoc.MediaService.dtos.responses.CloudinaryUploadResponseDTO;
import vn.tuhoc.MediaService.exceptions.FileNameIsEmptyException;
import vn.tuhoc.MediaService.exceptions.FileUploadIsEmptyException;
import vn.tuhoc.MediaService.services.interfaces.CloudinaryService;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryServiceImplement implements CloudinaryService {
    Cloudinary cloudinary;

    private String generatePublicId(String fileName) {
        return UUID.randomUUID() + "_" + fileName;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadIsEmptyException();
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new FileNameIsEmptyException();
        }
    }

    private String[] extractNameAndExtension(String originalName) {
        int lastDot = originalName.lastIndexOf(".");
        if (lastDot == -1) {
            return new String[] { originalName, "" };
        }
        if (lastDot == 0) {
            return new String[] { originalName, "" };
        }

        String name = originalName.substring(0, lastDot);
        String extension = originalName.substring(lastDot + 1);

        return new String[] { name, extension };
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }

        return ((Number) value).longValue();
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }

        return ((Number) value).intValue();
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }

        return Instant.parse(value.toString())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @Override
    public CloudinaryUploadResponseDTO upload(MultipartFile file, MediaTypeEnum mediaTypeEnum) {
        String mediaTypeEnumValue = mediaTypeEnum == MediaTypeEnum.FILE
                ? "raw"
                : mediaTypeEnum.getValue().toLowerCase();

        //
        this.validateFile(file);
        //
        String originalName = file.getOriginalFilename();
        String[] nameParts = this.extractNameAndExtension(originalName);
        String fileName = nameParts[0];
        String extension = nameParts[1];
        //
        String publicId = this.generatePublicId(fileName);

        @SuppressWarnings("unchecked")
        Map<Object, Object> options = ObjectUtils.asMap(
                "public_id", publicId,
                "resource_type", mediaTypeEnumValue,
                "folder", String.format("vinasocial/%s", mediaTypeEnumValue + "s"));
        if (!extension.isBlank()) {
            options.put("format", extension);
        }

        Map<?, ?> uploadResult;
        CloudinaryUploadResponseDTO cloudinaryUploadResponseDTO;
        try {
            uploadResult = this.cloudinary.uploader().upload(
                    file.getBytes(),
                    options);

            cloudinaryUploadResponseDTO = CloudinaryUploadResponseDTO.builder()
                    .publicId((String) uploadResult.get("public_id"))
                    .width(toInteger(uploadResult.get("width")))
                    .height(toInteger(uploadResult.get("height")))
                    .format((String) uploadResult.get("format"))
                    .resourceType((String) uploadResult.get("resource_type"))
                    .createdAt(toLocalDateTime(uploadResult.get("created_at")))
                    .bytes(toLong(uploadResult.get("bytes")))
                    .url((String) uploadResult.get("url"))
                    .secureUrl((String) uploadResult.get("secure_url"))
                    .originalFilename((String) uploadResult.get("original_filename"))
                    .build();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return cloudinaryUploadResponseDTO;
    }

    @Override
    public void delete(String publicId, MediaTypeEnum mediaTypeEnum) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }

        try {
            this.cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap("resource_type", mediaTypeEnum == MediaTypeEnum.FILE
                            ? "raw"
                            : mediaTypeEnum.getValue().toLowerCase()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
