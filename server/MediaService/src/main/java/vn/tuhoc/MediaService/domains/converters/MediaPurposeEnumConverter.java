package vn.tuhoc.MediaService.domains.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import vn.tuhoc.MediaService.domains.enums.MediaPurposeEnum;

@Component
public class MediaPurposeEnumConverter implements Converter<String, MediaPurposeEnum> {
    @Override
    public MediaPurposeEnum convert(String source) {
        return MediaPurposeEnum.fromDescription(source);
    }
}
