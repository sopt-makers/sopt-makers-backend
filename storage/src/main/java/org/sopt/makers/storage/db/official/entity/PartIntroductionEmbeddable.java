package org.sopt.makers.storage.db.official.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class PartIntroductionEmbeddable {

  @Column(name = "introduction_content", nullable = false, length = 2000)
  String content;

  @Column(name = "introduction_preference", nullable = false, length = 1000)
  String preference;
}
