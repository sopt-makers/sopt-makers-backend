package org.sopt.makers.storage.db.official.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.sopt.makers.domain.official.review.CategoryType;

@Converter
public class CategoryTypeConverter implements AttributeConverter<CategoryType, String> {

  @Override
  public String convertToDatabaseColumn(CategoryType attribute) {
    return attribute == null ? null : attribute.getDisplayName();
  }

  @Override
  public CategoryType convertToEntityAttribute(String dbData) {
    return dbData == null ? null : CategoryType.from(dbData);
  }
}
