package org.sopt.makers.storage.db.official.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class PartIntroductionEmbeddable {

  @Column(name = "introduction_content", nullable = false, length = 2000)
  String content;

  @Column(name = "introduction_preference", nullable = false, length = 1000)
  String preference;

  static PartIntroductionEmbeddable of(String content, List<String> preferences) {
    PartIntroductionEmbeddable e = new PartIntroductionEmbeddable();
    e.content = content != null ? content : "";
    e.preference =
        preferences == null || preferences.isEmpty()
            ? ""
            : preferences.stream().map(p -> "- " + p).collect(Collectors.joining("\n"));
    return e;
  }
}
