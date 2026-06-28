package org.sopt.makers.storage.db.official.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class BrandingColorEmbeddable {

  @Column(name = "\"darkModeKeyColor\"", length = 7)
  String darkModeKeyColor;

  @Column(name = "\"darkModeTextColor\"", length = 5)
  String darkModeTextColor;

  @Column(name = "\"lightModeKeyColor\"", length = 7)
  String lightModeKeyColor;

  @Column(name = "\"lightModeTextColor\"", length = 5)
  String lightModeTextColor;
}
