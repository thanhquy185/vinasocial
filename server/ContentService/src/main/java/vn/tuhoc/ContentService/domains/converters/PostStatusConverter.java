package vn.tuhoc.ContentService.domains.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.tuhoc.ContentService.domains.enums.PostStatusEnum;

@Converter(autoApply = true)
public class PostStatusConverter implements AttributeConverter<PostStatusEnum, String> {
    @Override
    public String convertToDatabaseColumn(PostStatusEnum status) {
        return (status != null) ? status.getValue() : null;
    }

    @Override
    public PostStatusEnum convertToEntityAttribute(String dbValue) {
        return (dbValue != null) ? PostStatusEnum.fromValue(dbValue) : null;
    }
}
