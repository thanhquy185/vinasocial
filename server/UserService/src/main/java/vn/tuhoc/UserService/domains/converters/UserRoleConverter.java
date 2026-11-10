package vn.tuhoc.UserService.domains.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.tuhoc.UserService.domains.enums.UserRoleEnum;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRoleEnum, String> {
    @Override
    public String convertToDatabaseColumn(UserRoleEnum role) {
        return (role != null) ? role.getValue() : null;
    }

    @Override
    public UserRoleEnum convertToEntityAttribute(String dbValue) {
        return (dbValue != null) ? UserRoleEnum.fromValue(dbValue) : null;
    }
}
