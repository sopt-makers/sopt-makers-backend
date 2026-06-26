package org.sopt.makers.domain.official.review;

import java.util.Arrays;
import java.util.List;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.official.review.exception.ReviewException;
import org.sopt.makers.domain.official.review.exception.ReviewFailure;

public final class ReviewPart {

  private static final List<Part> HOMEPAGE_PARTS =
      List.of(Part.IOS, Part.PLAN, Part.DESIGN, Part.SERVER, Part.ANDROID, Part.WEB, Part.COMMON);

  private ReviewPart() {}

  public static Part from(String input) {
    if (input == null || input.isBlank()) {
      throw new ReviewException(ReviewFailure.INVALID_PART);
    }

    return Arrays.stream(Part.values())
        .filter(part -> part.name().equalsIgnoreCase(input.trim()) || part.getName().equalsIgnoreCase(input.trim()))
        .findFirst()
        .orElseThrow(() -> new ReviewException(ReviewFailure.INVALID_PART));
  }

  public static Part fromNullable(String input) {
    if (input == null || input.isBlank()) {
      return null;
    }
    return from(input);
  }

  public static String displayName(Part part) {
    return part == null ? null : part.getName();
  }

  public static List<Part> homepageParts() {
    return HOMEPAGE_PARTS;
  }
}
