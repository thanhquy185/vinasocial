package vn.tuhoc.MediaService.dtos.responses;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloudinaryUploadResponseDTO {
    String publicId;

    Integer width;

    Integer height;

    String format;

    String resourceType;

    LocalDateTime createdAt;

    Long bytes;

    String url;

    String secureUrl;

    String originalFilename;
}
