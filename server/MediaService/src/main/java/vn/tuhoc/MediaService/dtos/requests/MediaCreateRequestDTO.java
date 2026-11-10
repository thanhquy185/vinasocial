package vn.tuhoc.MediaService.dtos.requests;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.MediaService.domains.enums.MediaPurposeEnum;
import vn.tuhoc.MediaService.domains.enums.MediaTypeEnum;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MediaCreateRequestDTO {
    @NotNull(message = "File không được để trống!")
    MultipartFile multipartFile;

    @NotNull(message = "Loại không được để trống!")
    MediaTypeEnum type;

    @NotNull(message = "Mục đích không được để trống!")
    MediaPurposeEnum purpose;
}
