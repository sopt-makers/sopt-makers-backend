package org.sopt.makers.domain.official.review;

import java.util.ArrayList;
import java.util.List;
import org.sopt.makers.core.type.Part;

public record CreateReviewCommand(
    Integer generation,
    Part part,
    String mainCategory,
    List<String> subActivities,
    String subRecruiting,
    String author,
    String authorProfileImageUrl,
    String link) {

  public CategoryType categoryType() {
    return CategoryType.from(mainCategory);
  }

  public List<String> subjects() {
    CategoryType categoryType = categoryType();
    List<String> subjects = new ArrayList<>();
    if (categoryType == CategoryType.ACTIVITY && subActivities != null) {
      subjects.addAll(subActivities);
    }
    if (categoryType == CategoryType.RECRUITING && subRecruiting != null) {
      subjects.add(subRecruiting);
    }
    return subjects;
  }
}
