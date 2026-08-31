package org.sopt.makers.storage.db.crew.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Map;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Converter
public class ObjectMapConverter implements AttributeConverter<Map<String, Object>, String> {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final TypeReference<Map<String, Object>> TYPE = new TypeReference<>() {};

  @Override
  public String convertToDatabaseColumn(Map<String, Object> attribute) {
    return MAPPER.writeValueAsString(attribute == null ? Map.of() : attribute);
  }

  @Override
  public Map<String, Object> convertToEntityAttribute(String dbData) {
    return dbData == null || dbData.isBlank() ? Map.of() : MAPPER.readValue(dbData, TYPE);
  }
}
