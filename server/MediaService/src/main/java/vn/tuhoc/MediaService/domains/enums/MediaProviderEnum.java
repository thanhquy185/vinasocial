package vn.tuhoc.MediaService.domains.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum MediaProviderEnum {
    MINIO("MINIO", "MinIO"),
    CLOUDINARY("CLOUDINARY", "Cloudinary");

    String value;
    String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static MediaProviderEnum fromDescription(String description) {
        for (MediaProviderEnum provider : values()) {
            if (provider.getDescription().equalsIgnoreCase(description)) {
                return provider;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + description);
    }

    public static MediaProviderEnum fromValue(String value) {
        for (MediaProviderEnum provider : values()) {
            if (provider.getValue().equalsIgnoreCase(value)) {
                return provider;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy: " + value);
    }
}
