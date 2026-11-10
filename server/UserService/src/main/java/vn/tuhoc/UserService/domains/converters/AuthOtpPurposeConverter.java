package vn.tuhoc.UserService.domains.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.tuhoc.UserService.domains.enums.AuthOtpPurposeEnum;

@Converter(autoApply = true)
public class AuthOtpPurposeConverter implements AttributeConverter<AuthOtpPurposeEnum, String> {
    @Override
    public String convertToDatabaseColumn(AuthOtpPurposeEnum purpose) {
        return (purpose != null) ? purpose.getValue() : null;
    }

    @Override
    public AuthOtpPurposeEnum convertToEntityAttribute(String dbValue) {
        return (dbValue != null) ? AuthOtpPurposeEnum.fromValue(dbValue) : null;
    }
}
