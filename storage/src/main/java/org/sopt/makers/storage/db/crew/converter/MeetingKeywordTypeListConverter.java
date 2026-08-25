package org.sopt.makers.storage.db.crew.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Converter
public class MeetingKeywordTypeListConverter
    implements AttributeConverter<List<MeetingKeywordType>, String> {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final TypeReference<List<MeetingKeywordType>> TYPE_REF = new TypeReference<>() {};

  @Override
  public String convertToDatabaseColumn(List<MeetingKeywordType> attribute) {
    return attribute == null ? null : MAPPER.writeValueAsString(attribute);
  }

  @Override
  public List<MeetingKeywordType> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isBlank()) {
      return List.of();
    }
    return MAPPER.readValue(dbData, TYPE_REF);
  }
}
