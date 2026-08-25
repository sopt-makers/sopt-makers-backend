package org.sopt.makers.storage.db.crew.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.sopt.makers.domain.crew.meeting.MeetingJoinInfo;
import tools.jackson.databind.ObjectMapper;

@Converter
public class MeetingJoinInfoConverter implements AttributeConverter<MeetingJoinInfo, String> {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(MeetingJoinInfo attribute) {
    if (attribute == null) {
      return null;
    }
    return MAPPER.writeValueAsString(attribute);
  }

  @Override
  public MeetingJoinInfo convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isBlank()) {
      return null;
    }
    return MAPPER.readValue(dbData, MeetingJoinInfo.class);
  }
}
