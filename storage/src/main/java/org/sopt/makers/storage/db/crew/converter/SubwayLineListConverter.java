package org.sopt.makers.storage.db.crew.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;
import org.sopt.makers.domain.crew.soptmap.SubwayLine;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Converter
public class SubwayLineListConverter implements AttributeConverter<List<SubwayLine>, String> {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final TypeReference<List<String>> TYPE = new TypeReference<>() {};

  @Override
  public String convertToDatabaseColumn(List<SubwayLine> attribute) {
    return MAPPER.writeValueAsString(
        attribute == null ? List.of() : attribute.stream().map(SubwayLine::getValue).toList());
  }

  @Override
  public List<SubwayLine> convertToEntityAttribute(String dbData) {
    return dbData == null || dbData.isBlank()
        ? List.of()
        : MAPPER.readValue(dbData, TYPE).stream().map(SubwayLine::fromValue).toList();
  }
}
