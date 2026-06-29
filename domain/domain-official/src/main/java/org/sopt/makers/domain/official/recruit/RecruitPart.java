package org.sopt.makers.domain.official.recruit;

import java.util.Arrays;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.official.recruit.exception.RecruitException;
import org.sopt.makers.domain.official.recruit.exception.RecruitFailure;

public final class RecruitPart {

  private RecruitPart() {}

  public static Part from(String input) {
    if (input == null || input.isBlank()) {
      throw new RecruitException(RecruitFailure.INVALID_PART);
    }

    return Arrays.stream(Part.values())
        .filter(part -> part.name().equalsIgnoreCase(input.trim()))
        .findFirst()
        .or(() -> Arrays.stream(Part.values()).filter(part -> part.getName().equalsIgnoreCase(input.trim())).findFirst())
        .orElseThrow(() -> new RecruitException(RecruitFailure.INVALID_PART));
  }

  public static String displayName(Part part) {
    return part == null ? null : part.getName();
  }
}
