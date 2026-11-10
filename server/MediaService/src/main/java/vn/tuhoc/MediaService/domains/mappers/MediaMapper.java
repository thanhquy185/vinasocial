package vn.tuhoc.MediaService.domains.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.tuhoc.MediaService.domains.entities.MediaEntity;
import vn.tuhoc.MediaService.dtos.requests.MediaCreateRequestDTO;
import vn.tuhoc.MediaService.dtos.responses.MediaDetailResponseDTO;
import vn.tuhoc.core.configs.CentralMapperConfig;

@Mapper(config = CentralMapperConfig.class)
public interface MediaMapper {
    MediaDetailResponseDTO entityToDetailResponse(MediaEntity mediaEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "objectKey", ignore = true)
    @Mapping(target = "originalFilename", ignore = true)
    @Mapping(target = "contentType", ignore = true)
    @Mapping(target = "width", ignore = true)
    @Mapping(target = "height", ignore = true)
    @Mapping(target = "encryptionMode", ignore = true)
    @Mapping(target = "encryptionAlgorithm", ignore = true)
    @Mapping(target = "encryptedIv", ignore = true)
    @Mapping(target = "encryptedAesKey", ignore = true)
    @Mapping(target = "isEncrypted", ignore = true)
    @Mapping(target = "status", ignore = true)
    MediaEntity createEntityFromRequest(MediaCreateRequestDTO mediaCreateRequestDTO);
}
