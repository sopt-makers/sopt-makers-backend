package org.sopt.makers.storage.db.crew.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;
import org.sopt.makers.domain.crew.meeting.tag.WelcomeMessageType;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Converter
public class WelcomeMessageTypeListConverter
    implements AttributeConverter<List<WelcomeMessageType>, String> {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final TypeReference<List<WelcomeMessageType>> TYPE_REF = new TypeReference<>() {};

  @Override
  public String convertToDatabaseColumn(List<WelcomeMessageType> attribute) {
    return attribute == null ? null : MAPPER.writeValueAsString(attribute);
  }

  @Override
  public List<WelcomeMessageType> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isBlank()) {
      return List.of();
    }
    return MAPPER.readValue(dbData, TYPE_REF);
  }
}
