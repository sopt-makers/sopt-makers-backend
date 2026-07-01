package org.sopt.makers.storage.db.official.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.official.generation.BrandingColor;

@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class BrandingColorEmbeddable {

  @Column(name = "dark_mode_key_color", nullable = false, length = 7)
  String darkModeKeyColor;

  @Column(name = "dark_mode_text_color", nullable = false, length = 5)
  String darkModeTextColor;

  @Column(name = "light_mode_key_color", nullable = false, length = 7)
  String lightModeKeyColor;

  @Column(name = "light_mode_text_color", nullable = false, length = 5)
  String lightModeTextColor;

  static BrandingColorEmbeddable of(BrandingColor bc) {
    BrandingColorEmbeddable e = new BrandingColorEmbeddable();
    e.darkModeKeyColor = bc.darkModeKeyColor();
    e.darkModeTextColor = bc.darkModeTextColor();
    e.lightModeKeyColor = bc.lightModeKeyColor();
    e.lightModeTextColor = bc.lightModeTextColor();
    return e;
  }
}
