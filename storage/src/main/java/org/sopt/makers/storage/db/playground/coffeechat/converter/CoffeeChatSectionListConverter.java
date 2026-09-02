package org.sopt.makers.storage.db.playground.coffeechat.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.sopt.makers.domain.playground.coffeechat.enums.CoffeeChatSection;

@Converter
public class CoffeeChatSectionListConverter
    implements AttributeConverter<List<CoffeeChatSection>, String> {

  @Override
  public String convertToDatabaseColumn(List<CoffeeChatSection> sections) {
    if (sections == null || sections.isEmpty()) {
      return "";
    }
    return sections.stream().map(Enum::name).collect(Collectors.joining(","));
  }

  @Override
  public List<CoffeeChatSection> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isEmpty()) {
      return List.of();
    }
    return Arrays.stream(dbData.split(","))
        .map(CoffeeChatSection::valueOf)
        .collect(Collectors.toList());
  }
}
