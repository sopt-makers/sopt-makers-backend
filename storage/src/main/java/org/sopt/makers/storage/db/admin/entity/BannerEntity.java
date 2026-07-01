package org.sopt.makers.storage.db.admin.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sopt.makers.domain.admin.banner.Banner;
import org.sopt.makers.domain.admin.banner.ContentType;
import org.sopt.makers.domain.admin.banner.PublishLocation;
import org.sopt.makers.storage.db.common.BaseEntity;

@Getter
@Entity
@Table(name = "banners")
@NoArgsConstructor(access = PROTECTED)
public class BannerEntity extends BaseEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(value = PROTECTED)
  private Long id;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PublishLocation location;

  @Column(name = "content_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private ContentType contentType;

  @Column(nullable = false)
  private String publisher;

  private String link;

  @Column(name = "pc_image_key")
  private String pcImageUrl;

  @Column(name = "mobile_image_key")
  private String mobileImageUrl;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "startDate", column = @Column(name = "start_date", nullable = false)),
    @AttributeOverride(name = "endDate", column = @Column(name = "end_date", nullable = false))
  })
  private PublishPeriodEmbeddable period;

  @Builder
  private BannerEntity(
      Long id,
      PublishLocation location,
      ContentType contentType,
      String publisher,
      String link,
      String pcImageUrl,
      String mobileImageUrl,
      PublishPeriodEmbeddable period) {
    setId(id);
    this.location = location;
    this.contentType = contentType;
    this.publisher = publisher;
    this.link = link;
    this.pcImageUrl = pcImageUrl;
    this.mobileImageUrl = mobileImageUrl;
    this.period = period;
  }

  public static BannerEntity from(Banner banner) {
    return BannerEntity.builder()
        .id(banner.getId())
        .location(banner.getLocation())
        .contentType(banner.getContentType())
        .publisher(banner.getPublisher())
        .link(banner.getLink())
        .pcImageUrl(banner.getPcImageUrl())
        .mobileImageUrl(banner.getMobileImageUrl())
        .period(PublishPeriodEmbeddable.from(banner.getPeriod()))
        .build();
  }

  public Banner toDomain() {
    return Banner.builder()
        .id(getId())
        .location(location)
        .contentType(contentType)
        .publisher(publisher)
        .link(link)
        .pcImageUrl(pcImageUrl)
        .mobileImageUrl(mobileImageUrl)
        .period(period.toDomain())
        .build();
  }
}
