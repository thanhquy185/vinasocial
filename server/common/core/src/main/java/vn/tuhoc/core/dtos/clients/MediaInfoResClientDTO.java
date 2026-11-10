package vn.tuhoc.core.dtos.clients;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MediaInfoResClientDTO {
    String id;

    String type;

    String purpose;

    String objectKey;

    String originalFilename;

    String contentType;

    Integer width;

    Integer height;
}
