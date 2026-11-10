package vn.tuhoc.MediaService.dtos.requests;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MediasCreateRequestDTO {
    @NotNull(message = "Danh sách media không được để trống!")
    @Size(min = 1, message = "Phải có ít nhất 1 media!")
    List<@Valid MediaCreateRequestDTO> mediaCreateRequestDTOs;
}
