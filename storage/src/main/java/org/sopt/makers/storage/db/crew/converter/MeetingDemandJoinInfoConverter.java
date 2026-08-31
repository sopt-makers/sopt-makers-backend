package org.sopt.makers.storage.db.crew.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandJoinInfo;
import tools.jackson.databind.ObjectMapper;

@Converter
public class MeetingDemandJoinInfoConverter
    implements AttributeConverter<MeetingDemandJoinInfo, String> {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(MeetingDemandJoinInfo attribute) {
    return attribute == null ? null : MAPPER.writeValueAsString(attribute);
  }

  @Override
  public MeetingDemandJoinInfo convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isBlank()) {
      return null;
    }
    return MAPPER.readValue(dbData, MeetingDemandJoinInfo.class);
  }
}
