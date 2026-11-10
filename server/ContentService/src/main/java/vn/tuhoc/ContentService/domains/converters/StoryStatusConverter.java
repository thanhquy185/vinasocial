package vn.tuhoc.ContentService.domains.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.tuhoc.ContentService.domains.enums.StoryStatusEnum;
import vn.tuhoc.core.utils.ValidationUtil;

@Converter(autoApply = true)
public class StoryStatusConverter implements AttributeConverter<StoryStatusEnum, String> {
    @Override
    public String convertToDatabaseColumn(StoryStatusEnum status) {
        return ValidationUtil.isNull(status) ? null : status.getValue();
    }

    @Override
    public StoryStatusEnum convertToEntityAttribute(String value) {
        return ValidationUtil.isNull(value) ? null : StoryStatusEnum.fromValue(value);
    }
}
