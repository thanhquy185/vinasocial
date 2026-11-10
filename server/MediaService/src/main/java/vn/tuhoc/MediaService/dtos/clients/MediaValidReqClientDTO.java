package vn.tuhoc.MediaService.dtos.clients;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.MediaService.domains.enums.MediaPurposeEnum;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MediaValidReqClientDTO {
    Integer userId;

    MediaPurposeEnum mediaPurpose;

    List<String> mediaIds;
}
