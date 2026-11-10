package vn.tuhoc.ContentService.domains.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.tuhoc.ContentService.domains.enums.PostTypeEnum;

@Converter(autoApply = true)
public class PostTypeConverter implements AttributeConverter<PostTypeEnum, String> {
    @Override
    public String convertToDatabaseColumn(PostTypeEnum type) {
        return (type != null) ? type.getValue() : null;
    }

    @Override
    public PostTypeEnum convertToEntityAttribute(String dbValue) {
        return (dbValue != null) ? PostTypeEnum.fromValue(dbValue) : null;
    }
}
