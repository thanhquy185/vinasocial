package vn.tuhoc.UserService.domains.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.tuhoc.UserService.domains.enums.ProfilePrivacySettingPermissionEnum;

@Converter(autoApply = true)
public class ProfilePrivacySettingPermissionConverter
        implements AttributeConverter<ProfilePrivacySettingPermissionEnum, String> {
    @Override
    public String convertToDatabaseColumn(ProfilePrivacySettingPermissionEnum permission) {
        return (permission != null) ? permission.getValue() : null;
    }

    @Override
    public ProfilePrivacySettingPermissionEnum convertToEntityAttribute(String dbValue) {
        return (dbValue != null) ? ProfilePrivacySettingPermissionEnum.fromValue(dbValue) : null;
    }
}
