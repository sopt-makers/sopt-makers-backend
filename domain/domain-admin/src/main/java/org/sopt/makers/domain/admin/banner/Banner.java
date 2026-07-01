package org.sopt.makers.domain.admin.banner;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Banner {

  private final Long id;
  private PublishLocation location;
  private ContentType contentType;
  private String publisher;
  private String link;
  private String pcImageUrl;
  private String mobileImageUrl;
  private PublishPeriod period;

  @Builder
  public Banner(
      Long id,
      PublishLocation location,
      ContentType contentType,
      String publisher,
      String link,
      String pcImageUrl,
      String mobileImageUrl,
      PublishPeriod period) {
    this.id = id;
    this.location = location;
    this.contentType = contentType;
    this.publisher = publisher;
    this.link = link;
    this.pcImageUrl = pcImageUrl;
    this.mobileImageUrl = mobileImageUrl;
    this.period = period;
  }

  public void update(
      PublishLocation location,
      ContentType contentType,
      String publisher,
      String link,
      PublishPeriod period,
      String pcImageUrl,
      String mobileImageUrl) {
    this.location = location;
    this.contentType = contentType;
    this.publisher = publisher;
    this.link = link;
    this.period = period;
    this.pcImageUrl = pcImageUrl;
    this.mobileImageUrl = mobileImageUrl;
  }
}
