package com.go2it.edu.config;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Convertion of Roles
 */
@Converter(autoApply = true)
public class RolesConverter implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List<String> roles) {
        StringBuilder sb = new StringBuilder();
        roles.forEach(sb::append);
        return sb.toString();
    }

    @Override
    public List<String> convertToEntityAttribute(String rolesString) {
        if (rolesString == null || rolesString.isEmpty()) {
            return Collections.emptyList();
        }
        String[] split = rolesString.split(",");
        return Arrays.asList(split);
    }
}
