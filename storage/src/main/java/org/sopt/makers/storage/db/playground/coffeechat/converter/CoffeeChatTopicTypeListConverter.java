package org.sopt.makers.storage.db.playground.coffeechat.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.sopt.makers.domain.playground.coffeechat.enums.CoffeeChatTopicType;

@Converter
public class CoffeeChatTopicTypeListConverter
    implements AttributeConverter<List<CoffeeChatTopicType>, String> {

  @Override
  public String convertToDatabaseColumn(List<CoffeeChatTopicType> topicTypes) {
    if (topicTypes == null || topicTypes.isEmpty()) {
      return "";
    }
    return topicTypes.stream().map(Enum::name).collect(Collectors.joining(","));
  }

  @Override
  public List<CoffeeChatTopicType> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isEmpty()) {
      return List.of();
    }
    return Arrays.stream(dbData.split(","))
        .map(CoffeeChatTopicType::valueOf)
        .collect(Collectors.toList());
  }
}
