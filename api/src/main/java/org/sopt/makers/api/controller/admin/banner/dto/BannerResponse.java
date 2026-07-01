package org.sopt.makers.api.controller.admin.banner.dto;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.admin.banner.service.BannerService;

@NoArgsConstructor(access = PRIVATE)
public final class BannerResponse {

  public record BannerSimple(
      @JsonProperty("id") long bannerId,
      @JsonProperty("status") String bannerStatus,
      @JsonProperty("location") String bannerLocation,
      @JsonProperty("content_type") String bannerType,
      @JsonProperty("publisher") String publisher,
      @JsonProperty("start_date") LocalDate startDate,
      @JsonProperty("end_date") LocalDate endDate) {

    public static BannerSimple from(BannerService.BannerSimple banner) {
      return new BannerSimple(
          banner.bannerId(),
          banner.bannerStatus(),
          banner.bannerLocation(),
          banner.bannerType(),
          banner.publisher(),
          banner.startDate(),
          banner.endDate());
    }
  }

  public record BannerDetail(
      @JsonProperty("id") long bannerId,
      @JsonProperty("status") String bannerStatus,
      @JsonProperty("location") String bannerLocation,
      @JsonProperty("content_type") String bannerType,
      @JsonProperty("publisher") String publisher,
      @JsonProperty("link") String link,
      @JsonProperty("start_date") LocalDate startDate,
      @JsonProperty("end_date") LocalDate endDate,
      @JsonProperty("image_url_pc") String pcImageUrl,
      @JsonProperty("image_url_mobile") String mobileImageUrl) {

    public static BannerDetail from(BannerService.BannerDetail banner) {
      return new BannerDetail(
          banner.bannerId(),
          banner.bannerStatus(),
          banner.bannerLocation(),
          banner.bannerType(),
          banner.publisher(),
          banner.link(),
          banner.startDate(),
          banner.endDate(),
          banner.pcImageUrl(),
          banner.mobileImageUrl());
    }
  }

  public record BannerImageWithBothPlatforms(
      @JsonProperty("pc_url") String pcUrl,
      @JsonProperty("mobile_url") String mobileUrl,
      @JsonProperty("start_date") LocalDate startDate,
      @JsonProperty("end_date") LocalDate endDate,
      @JsonProperty("link") String link) {

    public static BannerImageWithBothPlatforms from(
        BannerService.BannerImageWithBothPlatforms banner) {
      return new BannerImageWithBothPlatforms(
          banner.pcUrl(), banner.mobileUrl(), banner.startDate(), banner.endDate(), banner.link());
    }
  }

  public record BannerPage<T>(
      Integer limit,
      Integer totalCount,
      Integer totalPage,
      Integer currentPage,
      List<T> data,
      Boolean hasNextPage,
      Boolean hasPrevPage) {

    public static BannerPage<BannerSimple> from(
        BannerService.BannerPage<BannerService.BannerSimple> page) {
      return new BannerPage<>(
          page.limit(),
          page.totalCount(),
          page.totalPage(),
          page.currentPage(),
          page.data().stream().map(BannerSimple::from).toList(),
          page.hasNextPage(),
          page.hasPrevPage());
    }
  }
}
