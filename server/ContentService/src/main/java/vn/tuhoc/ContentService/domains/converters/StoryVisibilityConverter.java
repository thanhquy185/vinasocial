package vn.tuhoc.ContentService.domains.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.tuhoc.ContentService.domains.enums.StoryVisibilityEnum;
import vn.tuhoc.core.utils.ValidationUtil;

@Converter(autoApply = true)
public class StoryVisibilityConverter implements AttributeConverter<StoryVisibilityEnum, String> {
    @Override
    public String convertToDatabaseColumn(StoryVisibilityEnum visibility) {
        return ValidationUtil.isNull(visibility) ? null : visibility.getValue();
    }

    @Override
    public StoryVisibilityEnum convertToEntityAttribute(String value) {
        return ValidationUtil.isNull(value) ? null : StoryVisibilityEnum.fromValue(value);
    }
}
