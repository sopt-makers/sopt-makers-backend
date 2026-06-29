package org.sopt.makers.domain.official.recruit;

import java.util.List;
import org.sopt.makers.core.type.Part;

public record RecruitPartIntroduction(
    Long id, Integer generationId, Part part, String content, List<String> preferences) {

  public static RecruitPartIntroduction from(
      Long id, Integer generationId, Part part, String content, String preference) {
    return new RecruitPartIntroduction(
        id, generationId, part, content, parsePreferences(preference));
  }

  private static List<String> parsePreferences(String preference) {
    if (preference == null || preference.isBlank()) {
      return List.of();
    }
    return preference
        .lines()
        .map(line -> line.replaceFirst("^-\\s*", "").trim())
        .filter(line -> !line.isBlank())
        .toList();
  }
}
