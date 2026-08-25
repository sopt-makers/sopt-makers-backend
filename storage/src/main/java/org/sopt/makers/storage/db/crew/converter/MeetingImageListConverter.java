package org.sopt.makers.storage.db.crew.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;
import org.sopt.makers.domain.crew.meeting.MeetingImage;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Converter
public class MeetingImageListConverter implements AttributeConverter<List<MeetingImage>, String> {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final TypeReference<List<MeetingImage>> TYPE_REF = new TypeReference<>() {};

  @Override
  public String convertToDatabaseColumn(List<MeetingImage> attribute) {
    if (attribute == null) {
      return null;
    }
    return MAPPER.writeValueAsString(attribute);
  }

  @Override
  public List<MeetingImage> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isBlank()) {
      return List.of();
    }
    return MAPPER.readValue(dbData, TYPE_REF);
  }
}
