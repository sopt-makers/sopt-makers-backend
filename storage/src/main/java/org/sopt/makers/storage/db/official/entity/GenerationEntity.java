package org.sopt.makers.storage.db.official.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.official.generation.BrandingColor;
import org.sopt.makers.domain.official.generation.Generation;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "generation")
public class GenerationEntity {

  @Id
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "name", nullable = false, length = 50)
  private String name;

  @Column(name = "header_image", nullable = false, length = 500)
  private String headerImage;

  @Column(name = "recruit_header_image", nullable = false, length = 500)
  private String recruitHeaderImage;

  @Column(name = "home_header_image", nullable = false, length = 500)
  private String homeHeaderImage;

  @Embedded private BrandingColorEmbeddable brandingColor;

  public Generation toDomain() {
    return new Generation(
            id,
            name,
            headerImage,
            recruitHeaderImage,
            homeHeaderImage,
            brandingColor == null ? null : new BrandingColor(
                    brandingColor.darkModeKeyColor,
                    brandingColor.darkModeTextColor,
                    brandingColor.lightModeKeyColor,
                    brandingColor.lightModeTextColor));
  }
}
