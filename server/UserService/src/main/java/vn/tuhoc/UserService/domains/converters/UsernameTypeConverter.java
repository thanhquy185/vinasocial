package vn.tuhoc.UserService.domains.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.tuhoc.UserService.domains.enums.UsernameTypeEnum;

@Converter(autoApply = true)
public class UsernameTypeConverter implements AttributeConverter<UsernameTypeEnum, String> {
    @Override
    public String convertToDatabaseColumn(UsernameTypeEnum type) {
        return (type != null) ? type.getValue() : null;
    }

    @Override
    public UsernameTypeEnum convertToEntityAttribute(String dbValue) {
        return (dbValue != null) ? UsernameTypeEnum.fromValue(dbValue) : null;
    }
}
