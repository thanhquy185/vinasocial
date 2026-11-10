package vn.tuhoc.ContentService.domains.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.tuhoc.ContentService.domains.enums.PostModerationStatusEnum;

@Converter(autoApply = true)
public class PostModerationStatusConverter implements AttributeConverter<PostModerationStatusEnum, String> {
    @Override
    public String convertToDatabaseColumn(PostModerationStatusEnum moderationStatus) {
        return (moderationStatus != null) ? moderationStatus.getValue() : null;
    }

    @Override
    public PostModerationStatusEnum convertToEntityAttribute(String dbValue) {
        return (dbValue != null) ? PostModerationStatusEnum.fromValue(dbValue) : null;
    }
}
