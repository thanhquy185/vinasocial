package vn.tuhoc.MediaService.domains.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import vn.tuhoc.MediaService.domains.enums.MediaTypeEnum;

@Component
public class MediaTypeEnumConverter implements Converter<String, MediaTypeEnum> {
    @Override
    public MediaTypeEnum convert(String source) {
        return MediaTypeEnum.fromDescription(source);
    }
}
