package org.sopt.makers.storage.db.crew.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Converter
public class LongListConverter implements AttributeConverter<List<Long>, String> {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final TypeReference<List<Long>> TYPE = new TypeReference<>() {};

  @Override
  public String convertToDatabaseColumn(List<Long> attribute) {
    return MAPPER.writeValueAsString(attribute == null ? List.of() : attribute);
  }

  @Override
  public List<Long> convertToEntityAttribute(String dbData) {
    return dbData == null || dbData.isBlank() ? List.of() : MAPPER.readValue(dbData, TYPE);
  }
}
