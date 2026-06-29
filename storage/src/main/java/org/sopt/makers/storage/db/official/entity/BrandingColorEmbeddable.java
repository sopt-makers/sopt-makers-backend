package org.sopt.makers.storage.db.official.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class BrandingColorEmbeddable {

  @Column(name = "dark_mode_key_color", length = 7)
  String darkModeKeyColor;

  @Column(name = "dark_mode_text_color", length = 5)
  String darkModeTextColor;

  @Column(name = "light_mode_key_color", length = 7)
  String lightModeKeyColor;

  @Column(name = "light_mode_text_color", length = 5)
  String lightModeTextColor;
}
