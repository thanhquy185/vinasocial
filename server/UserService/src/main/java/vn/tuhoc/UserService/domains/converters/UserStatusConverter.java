package vn.tuhoc.UserService.domains.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.tuhoc.UserService.domains.enums.UserStatusEnum;

@Converter(autoApply = true)
public class UserStatusConverter implements AttributeConverter<UserStatusEnum, String> {
    @Override
    public String convertToDatabaseColumn(UserStatusEnum status) {
        return (status != null) ? status.getValue() : null;
    }

    @Override
    public UserStatusEnum convertToEntityAttribute(String dbValue) {
        return (dbValue != null) ? UserStatusEnum.fromValue(dbValue) : null;
    }
}
