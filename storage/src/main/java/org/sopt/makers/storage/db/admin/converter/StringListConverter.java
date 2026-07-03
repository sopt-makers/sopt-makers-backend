package org.sopt.makers.storage.db.admin.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static final TypeReference<List<String>> TYPE_REF = new TypeReference<>() {};

  @Override
  public String convertToDatabaseColumn(List<String> attribute) {
    if (attribute == null) {
      return null;
    }
    return mapper.writeValueAsString(attribute);
  }

  @Override
  public List<String> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isBlank()) {
      return null;
    }
    return mapper.readValue(dbData, TYPE_REF);
  }
}
