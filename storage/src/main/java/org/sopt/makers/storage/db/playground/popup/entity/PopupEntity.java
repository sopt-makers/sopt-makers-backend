package org.sopt.makers.storage.db.playground.popup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.playground.popup.Popup;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Table(name = "popup")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PopupEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDate startDate;

  @Column(nullable = false)
  private LocalDate endDate;

  @Column(nullable = false)
  private String pcImageUrl;

  @Column(nullable = false)
  private String mobileImageUrl;

  @Column
  private String linkUrl;

  @Column
  private Boolean openInNewTab;

  @Column
  private Boolean showOnlyToRecentGeneration;

  private PopupEntity(
      Long id,
      LocalDate startDate,
      LocalDate endDate,
      String pcImageUrl,
      String mobileImageUrl,
      String linkUrl,
      Boolean openInNewTab,
      Boolean showOnlyToRecentGeneration) {
    this.id = id;
    this.startDate = startDate;
    this.endDate = endDate;
    this.pcImageUrl = pcImageUrl;
    this.mobileImageUrl = mobileImageUrl;
    this.linkUrl = linkUrl;
    this.openInNewTab = openInNewTab;
    this.showOnlyToRecentGeneration = showOnlyToRecentGeneration;
  }

  public static PopupEntity from(Popup popup) {
    return new PopupEntity(
        popup.id(),
        popup.startDate(),
        popup.endDate(),
        popup.pcImageUrl(),
        popup.mobileImageUrl(),
        popup.linkUrl(),
        popup.openInNewTab(),
        popup.showOnlyToRecentGeneration());
  }

  public Popup toDomain() {
    return new Popup(
        id,
        startDate,
        endDate,
        pcImageUrl,
        mobileImageUrl,
        linkUrl,
        openInNewTab,
        showOnlyToRecentGeneration,
        getCreatedAt(),
        getUpdatedAt());
  }
}
