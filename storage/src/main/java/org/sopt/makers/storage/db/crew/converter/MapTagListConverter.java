package org.sopt.makers.storage.db.crew.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;
import org.sopt.makers.domain.crew.soptmap.MapTag;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Converter
public class MapTagListConverter implements AttributeConverter<List<MapTag>, String> {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final TypeReference<List<MapTag>> TYPE = new TypeReference<>() {};

  @Override
  public String convertToDatabaseColumn(List<MapTag> attribute) {
    return MAPPER.writeValueAsString(attribute == null ? List.of() : attribute);
  }

  @Override
  public List<MapTag> convertToEntityAttribute(String dbData) {
    return dbData == null || dbData.isBlank() ? List.of() : MAPPER.readValue(dbData, TYPE);
  }
}
