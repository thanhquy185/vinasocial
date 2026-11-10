package vn.tuhoc.ContentService.domains.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.tuhoc.ContentService.domains.enums.StoryModerationStatusEnum;
import vn.tuhoc.core.utils.ValidationUtil;

@Converter(autoApply = true)
public class StoryModerationStatusConverter implements AttributeConverter<StoryModerationStatusEnum, String> {
    @Override
    public String convertToDatabaseColumn(StoryModerationStatusEnum status) {
        return ValidationUtil.isNull(status) ? null : status.getValue();
    }

    @Override
    public StoryModerationStatusEnum convertToEntityAttribute(String value) {
        return ValidationUtil.isNull(value) ? null : StoryModerationStatusEnum.fromValue(value);
    }
}
