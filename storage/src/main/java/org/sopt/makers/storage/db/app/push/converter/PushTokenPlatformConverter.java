package org.sopt.makers.storage.db.app.push.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import org.sopt.makers.domain.app.push.PushTokenPlatform;

@Converter(autoApply = false)
public class PushTokenPlatformConverter implements AttributeConverter<PushTokenPlatform, String> {

  private static final String ANDROID = "Android";
  private static final String IOS = "iOS";

  @Override
  public String convertToDatabaseColumn(PushTokenPlatform attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute == PushTokenPlatform.IOS ? IOS : ANDROID;
  }

  @Override
  public PushTokenPlatform convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    return Arrays.stream(PushTokenPlatform.values())
        .filter(platform -> platform.name().equalsIgnoreCase(dbData))
        .findFirst()
        .orElse(null);
  }
}
