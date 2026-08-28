package org.sopt.makers.storage.db.crew.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.advertisement.Advertisement;
import org.sopt.makers.domain.crew.advertisement.AdvertisementCategory;
import org.sopt.makers.domain.crew.advertisement.AdvertisementEventType;
import org.sopt.makers.domain.crew.advertisement.AdvertisementTargetGeneration;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "advertisement")
public class AdvertisementEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "advertisement_desktop_image_url", nullable = false)
  private String desktopImageUrl;

  @Column(name = "advertisement_mobile_image_url", nullable = false)
  private String mobileImageUrl;

  @Column(name = "advertisement_link")
  private String advertisementLink;

  @Column(name = "calendar_image_url")
  private String calendarImageUrl;

  @Column(name = "title_prefix")
  private String titlePrefix;

  @Column(name = "title_highlight")
  private String titleHighlight;

  @Column(name = "title_suffix")
  private String titleSuffix;

  @Column(name = "sub_title")
  private String subTitle;

  @Enumerated(EnumType.STRING)
  @Column(name = "advertisement_category", nullable = false)
  private AdvertisementCategory category;

  @Column(nullable = false)
  private Long priority;

  @Column(name = "advertisement_start_date", nullable = false)
  private LocalDateTime startDate;

  @Column(name = "advertisement_end_date", nullable = false)
  private LocalDateTime endDate;

  @Column(name = "is_sponsored_content", nullable = false)
  private boolean sponsoredContent;

  @Column(name = "is_display", nullable = false)
  private boolean display;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_type")
  private AdvertisementEventType eventType;

  @Enumerated(EnumType.STRING)
  @Column(name = "target_generation", nullable = false)
  private AdvertisementTargetGeneration targetGeneration;

  public Advertisement toDomain() {
    return new Advertisement(
        id,
        desktopImageUrl,
        mobileImageUrl,
        advertisementLink,
        calendarImageUrl,
        titlePrefix,
        titleHighlight,
        titleSuffix,
        subTitle,
        category,
        priority,
        startDate,
        endDate,
        sponsoredContent,
        display,
        eventType,
        targetGeneration,
        getCreatedAt(),
        getUpdatedAt());
  }

  public void update(Advertisement advertisement) {
    desktopImageUrl = advertisement.desktopImageUrl();
    mobileImageUrl = advertisement.mobileImageUrl();
    calendarImageUrl = advertisement.calendarImageUrl();
    titlePrefix = advertisement.titlePrefix();
    titleHighlight = advertisement.titleHighlight();
    titleSuffix = advertisement.titleSuffix();
    subTitle = advertisement.subTitle();
    startDate = advertisement.startDate();
    endDate = advertisement.endDate();
    display = advertisement.display();
  }
}
