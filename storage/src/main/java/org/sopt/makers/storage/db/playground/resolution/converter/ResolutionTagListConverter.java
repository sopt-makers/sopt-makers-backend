package org.sopt.makers.storage.db.playground.resolution.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.sopt.makers.domain.playground.resolution.ResolutionTag;

@Converter
public class ResolutionTagListConverter implements AttributeConverter<List<ResolutionTag>, String> {

  @Override
  public String convertToDatabaseColumn(List<ResolutionTag> tags) {
    if (tags == null || tags.isEmpty()) {
      return "";
    }
    return tags.stream().map(Enum::name).collect(Collectors.joining(","));
  }

  @Override
  public List<ResolutionTag> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isEmpty()) {
      return List.of();
    }
    return Arrays.stream(dbData.split(","))
        .map(ResolutionTag::valueOf)
        .collect(Collectors.toList());
  }
}
