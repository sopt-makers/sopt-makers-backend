package org.sopt.makers.storage.db.official.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;
import org.sopt.makers.domain.official.faq.Faq;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Converter
public class FaqQuestionsConverter implements AttributeConverter<List<Faq.QuestionAnswer>, String> {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final TypeReference<List<Faq.QuestionAnswer>> TYPE = new TypeReference<>() {};

  @Override
  public String convertToDatabaseColumn(List<Faq.QuestionAnswer> questions) {
    return MAPPER.writeValueAsString(questions == null ? List.of() : questions);
  }

  @Override
  public List<Faq.QuestionAnswer> convertToEntityAttribute(String data) {
    return data == null || data.isBlank() ? List.of() : MAPPER.readValue(data, TYPE);
  }
}
