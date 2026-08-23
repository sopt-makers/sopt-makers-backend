package org.sopt.makers.domain.playground.resolution;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.resolution.exception.ResolutionException;
import org.sopt.makers.domain.playground.resolution.exception.ResolutionFailure;

@Getter
@RequiredArgsConstructor
public enum ResolutionTag {
  PRODUCT_RELEASE("제품 출시"),
  NETWORKING("네트워킹"),
  COLLABORATION_EXPERIENCE("협업 경험"),
  STARTUP("창업"),
  SKILL_UP("스킬업");

  private final String description;

  public static ResolutionTag fromString(String value) {
    try {
      return ResolutionTag.valueOf(value);
    } catch (IllegalArgumentException e) {
      throw new ResolutionException(ResolutionFailure.INVALID_RESOLUTION_TAG);
    }
  }

  public static List<ResolutionTag> fromStringList(List<String> values) {
    return values.stream().map(ResolutionTag::fromString).toList();
  }
}
