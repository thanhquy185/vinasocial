package vn.tuhoc.UserService.domains.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.tuhoc.UserService.domains.enums.ProfileGenderEnum;

@Converter(autoApply = true)
public class ProfileGenderConverter implements AttributeConverter<ProfileGenderEnum, String> {
    @Override
    public String convertToDatabaseColumn(ProfileGenderEnum gender) {
        return (gender != null) ? gender.getValue() : null;
    }

    @Override
    public ProfileGenderEnum convertToEntityAttribute(String dbValue) {
        return (dbValue != null) ? ProfileGenderEnum.fromValue(dbValue) : null;
    }
}
