package vn.tuhoc.ContentService.dtos.clients;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.enums.MediaPurposeEnum;

@Builder
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
