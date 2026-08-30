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
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "generation")
public class GenerationEntity extends BaseEntity {

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
        brandingColor == null
            ? null
            : new BrandingColor(
                brandingColor.darkModeKeyColor,
                brandingColor.darkModeTextColor,
                brandingColor.lightModeKeyColor,
                brandingColor.lightModeTextColor));
  }

  public static GenerationEntity create(Integer id, String name, BrandingColor brandingColor) {
    GenerationEntity entity = new GenerationEntity();
    entity.id = id;
    entity.name = name;
    entity.headerImage = "";
    entity.recruitHeaderImage = "";
    entity.homeHeaderImage = "";
    entity.brandingColor = brandingColor != null ? BrandingColorEmbeddable.of(brandingColor) : null;
    return entity;
  }

  public void update(String name, BrandingColor brandingColor) {
    this.name = name;
    this.brandingColor =
        brandingColor != null ? BrandingColorEmbeddable.of(brandingColor) : this.brandingColor;
  }

  public void updateHeaderImage(String headerImage) {
    this.headerImage = headerImage;
  }

  public void updateHomeHeaderImage(String homeHeaderImage) {
    this.homeHeaderImage = homeHeaderImage;
  }

  public void updateRecruitHeaderImage(String recruitHeaderImage) {
    this.recruitHeaderImage = recruitHeaderImage;
  }
}
