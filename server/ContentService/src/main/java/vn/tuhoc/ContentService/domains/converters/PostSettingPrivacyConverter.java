package vn.tuhoc.ContentService.domains.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.tuhoc.ContentService.domains.enums.PostSettingPrivacyEnum;

@Converter(autoApply = true)
public class PostSettingPrivacyConverter implements AttributeConverter<PostSettingPrivacyEnum, String> {
    @Override
    public String convertToDatabaseColumn(PostSettingPrivacyEnum settingPrivacy) {
        return (settingPrivacy != null) ? settingPrivacy.getValue() : null;
    }

    @Override
    public PostSettingPrivacyEnum convertToEntityAttribute(String dbValue) {
        return (dbValue != null) ? PostSettingPrivacyEnum.fromValue(dbValue) : null;
    }
}
