package vn.tuhoc.MediaService.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.MediaService.domains.entities.MediaEntity;
import vn.tuhoc.MediaService.domains.enums.MediaEncryptionModeEnum;
import vn.tuhoc.MediaService.domains.enums.MediaProviderEnum;
import vn.tuhoc.MediaService.domains.enums.MediaStatusEnum;
import vn.tuhoc.MediaService.domains.mappers.MediaMapper;
import vn.tuhoc.MediaService.dtos.clients.MediaValidReqClientDTO;
import vn.tuhoc.MediaService.dtos.requests.MediaCreateRequestDTO;
import vn.tuhoc.MediaService.dtos.requests.MediasCreateRequestDTO;
import vn.tuhoc.MediaService.dtos.responses.CloudinaryUploadResponseDTO;
import vn.tuhoc.MediaService.dtos.responses.MediaDetailResponseDTO;
import vn.tuhoc.MediaService.exceptions.MediaEncryptionPolicyViolationException;
import vn.tuhoc.MediaService.exceptions.MediaNotFoundByIdException;
import vn.tuhoc.MediaService.repositories.MediaRepository;
import vn.tuhoc.MediaService.services.interfaces.CloudinaryService;
import vn.tuhoc.MediaService.services.interfaces.MediaService;
import vn.tuhoc.core.dtos.clients.MediaInfoReqClientDTO;
import vn.tuhoc.core.dtos.clients.MediaInfoResClientDTO;
import vn.tuhoc.core.utils.TimeUtil;
import vn.tuhoc.core.utils.ValidationUtil;
import vn.tuhoc.security.utils.SecurityUtil;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MediaServiceImplement implements MediaService {
    CloudinaryService cloudinaryService;
    MediaRepository mediaRepository;
    MediaMapper mediaMapper;

    private MediaEntity getById(String id) {
        return this.mediaRepository.findById(id)
                .orElseThrow(() -> new MediaNotFoundByIdException(id));
    }

    private void validateEncryptionPolicy(Boolean isEncrypt, Boolean isE2EE, MediaCreateRequestDTO request) {
        if (isEncrypt == null || isE2EE == null || request == null || request.getPurpose() == null) {
            throw new MediaEncryptionPolicyViolationException(
                    "Thiếu thông tin bắt buộc để xác định chính sách mã hóa media.");
        }

        boolean encrypt = Boolean.TRUE.equals(isEncrypt);
        boolean e2ee = Boolean.TRUE.equals(isE2EE);

        if (e2ee && !encrypt) {
            throw new MediaEncryptionPolicyViolationException(
                    "Media E2EE phải được mã hóa trên thiết bị trước khi tải lên.");
        }

        if (encrypt && !e2ee) {
            throw new MediaEncryptionPolicyViolationException(
                    "Mã hóa media phía máy chủ chưa được hỗ trợ; yêu cầu này không thể tải lên an toàn.");
        }
    }

    @Override
    public Boolean handleValidate(MediaValidReqClientDTO mediaValidReqClientDTO) {
        if (ValidationUtil.isNull(mediaValidReqClientDTO)
                || ValidationUtil.isNull(mediaValidReqClientDTO.getUserId())
                || ValidationUtil.isNull(mediaValidReqClientDTO.getMediaPurpose())
                || ValidationUtil.isNull(mediaValidReqClientDTO.getMediaIds())
                || mediaValidReqClientDTO.getMediaIds().isEmpty()) {
            return false;
        }

        List<String> mediaIds = mediaValidReqClientDTO.getMediaIds();
        // Tránh mediaId bị duplicate
        if (mediaIds.stream().distinct().count() != mediaIds.size()) {
            return false;
        }

        List<MediaEntity> mediaEntities = this.mediaRepository.findAllByIdInAndUserIdAndPurposeAndStatus(
                mediaIds,
                mediaValidReqClientDTO.getUserId(),
                mediaValidReqClientDTO.getMediaPurpose(),
                MediaStatusEnum.SUCCEED);

        return mediaEntities.size() == mediaIds.size();
    }

    @Override
    public List<MediaInfoResClientDTO> handleGetInfo(MediaInfoReqClientDTO mediaInfoReqClientDTO) {
        return mediaRepository.findMediaInfoByIds(mediaInfoReqClientDTO.getMediaIds())
                .stream()
                .map(media -> MediaInfoResClientDTO.builder()
                        .id(media.getId())
                        .type(media.getType().getDescription())
                        .purpose(media.getPurpose().getDescription())
                        .objectKey(media.getObjectKey())
                        .originalFilename(media.getOriginalFilename())
                        .contentType(media.getContentType())
                        .width(media.getWidth())
                        .height(media.getHeight())
                        .build())
                .toList();
    }

    @Override
    public MediaDetailResponseDTO handleGetById(String id) {
        return this.mediaMapper.entityToDetailResponse(this.getById(id));
    }

    @Override
    public MediaDetailResponseDTO handleCreate(
            Boolean isEncrypt,
            Boolean isE2EE,
            MediaCreateRequestDTO mediaCreateRequestDTO) {
        this.validateEncryptionPolicy(isEncrypt, isE2EE, mediaCreateRequestDTO);
        Integer currentUserLoginId = SecurityUtil.getCurrentUserId().get();

        MediaEntity mediaEntity = this.mediaMapper.createEntityFromRequest(mediaCreateRequestDTO);
        mediaEntity.setUserId(currentUserLoginId);

        if (isEncrypt) {
            if (isE2EE) {
                // The client encrypts the bytes before upload. Store the opaque payload as a raw object;
                // the AES key and IV are carried inside the encrypted message envelope.
                CloudinaryUploadResponseDTO upload = this.cloudinaryService.upload(
                        mediaCreateRequestDTO.getMultipartFile(),
                        vn.tuhoc.MediaService.domains.enums.MediaTypeEnum.FILE);
                mediaEntity.setCreatedAt(TimeUtil.getDatetime(upload.getCreatedAt()));
                mediaEntity.setProvider(MediaProviderEnum.CLOUDINARY);
                mediaEntity.setObjectKey(upload.getSecureUrl());
                mediaEntity.setPublicId(upload.getPublicId());
                mediaEntity.setOriginalFilename(upload.getOriginalFilename());
                mediaEntity.setContentType("application/octet-stream");
                mediaEntity.setWidth(null);
                mediaEntity.setHeight(null);
                mediaEntity.setEncryptionMode(MediaEncryptionModeEnum.E2EE);
                mediaEntity.setEncryptionAlgorithm(
                        vn.tuhoc.MediaService.domains.enums.MediaEncryptionAlgorithmEnum.AES_256_GCM);
                mediaEntity.setEncryptedIv(null);
                mediaEntity.setEncryptedAesKey(null);
                mediaEntity.setIsEncrypted(true);
                mediaEntity.setStatus(MediaStatusEnum.SUCCEED);
            }
        } else {
            CloudinaryUploadResponseDTO cloudinaryUploadResponseDTO = this.cloudinaryService.upload(
                    mediaCreateRequestDTO.getMultipartFile(),
                    mediaCreateRequestDTO.getType());

            mediaEntity.setCreatedAt(TimeUtil.getDatetime(cloudinaryUploadResponseDTO.getCreatedAt()));
            mediaEntity.setProvider(MediaProviderEnum.CLOUDINARY);
            mediaEntity.setObjectKey(cloudinaryUploadResponseDTO.getSecureUrl());
            mediaEntity.setPublicId(cloudinaryUploadResponseDTO.getPublicId());
            mediaEntity.setOriginalFilename(cloudinaryUploadResponseDTO.getOriginalFilename());
            mediaEntity.setContentType(String.format(
                    "%s/%s",
                    cloudinaryUploadResponseDTO.getResourceType(),
                    cloudinaryUploadResponseDTO.getFormat()));
            mediaEntity.setWidth(cloudinaryUploadResponseDTO.getWidth());
            mediaEntity.setHeight(cloudinaryUploadResponseDTO.getHeight());
            mediaEntity.setEncryptionMode(MediaEncryptionModeEnum.NONE);
            mediaEntity.setEncryptionAlgorithm(null);
            mediaEntity.setEncryptedIv(null);
            mediaEntity.setEncryptedAesKey(null);
            mediaEntity.setIsEncrypted(false);
            mediaEntity.setStatus(MediaStatusEnum.SUCCEED);
        }

        return this.mediaMapper.entityToDetailResponse(this.mediaRepository.save(mediaEntity));
    }

    @Override
    public List<MediaDetailResponseDTO> handleCreateMedias(
            Boolean isEncrypt,
            Boolean isE2EE,
            MediasCreateRequestDTO mediasCreateRequestDTO) {
        mediasCreateRequestDTO.getMediaCreateRequestDTOs()
                .forEach(request -> this.validateEncryptionPolicy(isEncrypt, isE2EE, request));

        List<MediaDetailResponseDTO> list = new ArrayList<>();
        mediasCreateRequestDTO.getMediaCreateRequestDTOs().stream().forEach(mediaCreateRequestDTO -> {
            list.add(this.handleCreate(isEncrypt, isE2EE, mediaCreateRequestDTO));
        });

        return list;
    }
}
