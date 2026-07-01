package org.sopt.makers.domain.admin.banner.service;

import static org.sopt.makers.domain.admin.banner.exception.BannerFailure.CANNOT_DELETE_DONE_BANNER;
import static org.sopt.makers.domain.admin.banner.exception.BannerFailure.CANNOT_MODIFY_DONE_BANNER;
import static org.sopt.makers.domain.admin.banner.exception.BannerFailure.FILE_UPLOAD_FAILED;
import static org.sopt.makers.domain.admin.banner.exception.BannerFailure.INVALID_BANNER_DATE_FORMAT;
import static org.sopt.makers.domain.admin.banner.exception.BannerFailure.INVALID_BANNER_PROGRESS_STATUS_PARAMETER;
import static org.sopt.makers.domain.admin.banner.exception.BannerFailure.INVALID_BANNER_SORT_CRITERIA_PARAMETER;
import static org.sopt.makers.domain.admin.banner.exception.BannerFailure.NOT_FOUND_BANNER;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.banner.Banner;
import org.sopt.makers.domain.admin.banner.ContentType;
import org.sopt.makers.domain.admin.banner.PublishLocation;
import org.sopt.makers.domain.admin.banner.PublishPeriod;
import org.sopt.makers.domain.admin.banner.PublishStatus;
import org.sopt.makers.domain.admin.banner.exception.BannerException;
import org.sopt.makers.domain.admin.banner.port.BannerFileStoragePort;
import org.sopt.makers.domain.admin.banner.port.BannerRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BannerService {

  private static final String BANNER_IMAGE_DIRECTORY = "banners-images/";

  private final BannerRepositoryPort bannerRepositoryPort;
  private final BannerFileStoragePort bannerFileStoragePort;
  private final Clock clock;

  public BannerDetail getBannerDetail(long bannerId) {
    Banner banner = getBannerById(bannerId);
    return BannerDetail.from(
        banner,
        bannerFileStoragePort.getUrl(banner.getPcImageUrl()),
        bannerFileStoragePort.getUrl(banner.getMobileImageUrl()),
        LocalDate.now(clock));
  }

  @Transactional
  public void deleteBanner(long bannerId) {
    Banner banner = getBannerById(bannerId);
    PublishStatus bannerStatus = banner.getPeriod().getPublishStatus(LocalDate.now(clock));
    if (PublishStatus.DONE.equals(bannerStatus)) {
      throw new BannerException(CANNOT_DELETE_DONE_BANNER);
    }

    deleteImageIfExists(banner.getPcImageUrl());
    deleteImageIfExists(banner.getMobileImageUrl());
    bannerRepositoryPort.delete(banner);
  }

  public List<BannerImageWithBothPlatforms> getExternalBanners(String location) {
    PublishLocation publishLocation = PublishLocation.getByValue(location);
    LocalDate currentDate = LocalDate.now(clock);
    return bannerRepositoryPort.findByLocation(publishLocation).stream()
        .filter(
            banner -> banner.getPeriod().getPublishStatus(currentDate) == PublishStatus.IN_PROGRESS)
        .sorted(Comparator.comparing(Banner::getId))
        .map(
            banner ->
                BannerImageWithBothPlatforms.from(
                    banner,
                    bannerFileStoragePort.getUrl(banner.getPcImageUrl()),
                    bannerFileStoragePort.getUrl(banner.getMobileImageUrl())))
        .toList();
  }

  @Transactional
  public BannerDetail createBanner(BannerCreateOrModifyCommand command) {
    String pcImageUrl = upload(command.pcImage());
    String mobileImageUrl = upload(command.mobileImage());

    Banner banner =
        Banner.builder()
            .publisher(command.publisher())
            .link(command.link())
            .contentType(ContentType.getByValue(command.contentType()))
            .location(PublishLocation.getByValue(command.location()))
            .period(getPublishPeriod(command.startDate(), command.endDate()))
            .pcImageUrl(pcImageUrl)
            .mobileImageUrl(mobileImageUrl)
            .build();

    Banner saved = bannerRepositoryPort.save(banner);
    return BannerDetail.from(
        saved,
        bannerFileStoragePort.getUrl(saved.getPcImageUrl()),
        bannerFileStoragePort.getUrl(saved.getMobileImageUrl()),
        LocalDate.now(clock));
  }

  @Transactional
  public BannerDetail updateBanner(Long bannerId, BannerCreateOrModifyCommand command) {
    Banner banner = getBannerById(bannerId);
    PublishStatus bannerStatus = banner.getPeriod().getPublishStatus(LocalDate.now(clock));
    if (PublishStatus.DONE.equals(bannerStatus)) {
      throw new BannerException(CANNOT_MODIFY_DONE_BANNER);
    }

    String oldPcImageUrl = banner.getPcImageUrl();
    String oldMobileImageUrl = banner.getMobileImageUrl();
    String pcImageUrl = upload(command.pcImage());
    String mobileImageUrl = upload(command.mobileImage());

    banner.update(
        PublishLocation.getByValue(command.location()),
        ContentType.getByValue(command.contentType()),
        command.publisher(),
        command.link(),
        getPublishPeriod(command.startDate(), command.endDate()),
        pcImageUrl,
        mobileImageUrl);

    Banner saved = bannerRepositoryPort.save(banner);
    deleteImageIfExists(oldPcImageUrl);
    deleteImageIfExists(oldMobileImageUrl);

    return BannerDetail.from(
        saved,
        bannerFileStoragePort.getUrl(saved.getPcImageUrl()),
        bannerFileStoragePort.getUrl(saved.getMobileImageUrl()),
        LocalDate.now(clock));
  }

  public BannerPage<BannerSimple> getBannersWithPagination(
      FilterCriteria filter, SortCriteria sort, int page, int limit) {
    List<Banner> filteredBanners = getFilteredBanners(bannerRepositoryPort.findAll(), filter);
    List<Banner> sortedBanners = getSortedBanners(filteredBanners, sort);

    int totalCount = sortedBanners.size();
    int startIndex = (page - 1) * limit;
    int endIndex = Math.min(startIndex + limit, totalCount);
    if (startIndex > totalCount) {
      return new BannerPage<>(List.of(), totalCount, limit, page);
    }

    LocalDate currentDate = LocalDate.now(clock);
    List<BannerSimple> paginatedBanners =
        sortedBanners.subList(startIndex, endIndex).stream()
            .map(banner -> BannerSimple.from(banner, currentDate))
            .toList();

    return new BannerPage<>(paginatedBanners, totalCount, limit, page);
  }

  private Banner getBannerById(long id) {
    return bannerRepositoryPort
        .findById(id)
        .orElseThrow(() -> new BannerException(NOT_FOUND_BANNER));
  }

  private String upload(BannerFileStoragePort.UploadFile file) {
    try {
      return bannerFileStoragePort.upload(file, BANNER_IMAGE_DIRECTORY);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new BannerException(FILE_UPLOAD_FAILED);
    }
  }

  private void deleteImageIfExists(String imageUrl) {
    if (imageUrl != null && !imageUrl.isBlank()) {
      bannerFileStoragePort.delete(imageUrl);
    }
  }

  private List<Banner> getFilteredBanners(List<Banner> banners, FilterCriteria filter) {
    if (FilterCriteria.ALL.equals(filter)) {
      return banners;
    }
    PublishStatus targetStatus = PublishStatus.getByValue(filter.parameter);
    LocalDate currentDate = LocalDate.now(clock);
    return banners.stream()
        .filter(banner -> targetStatus.equals(banner.getPeriod().getPublishStatus(currentDate)))
        .toList();
  }

  private List<Banner> getSortedBanners(List<Banner> banners, SortCriteria criteria) {
    return switch (criteria) {
      case STATUS, START_DATE ->
          banners.stream()
              .sorted(
                  Comparator.comparing(
                          Banner::getPeriod, Comparator.comparing(PublishPeriod::startDate))
                      .thenComparing(
                          Banner::getPeriod, Comparator.comparing(PublishPeriod::endDate)))
              .toList();
      case END_DATE ->
          banners.stream()
              .sorted(
                  Comparator.comparing(
                          Banner::getPeriod, Comparator.comparing(PublishPeriod::endDate))
                      .thenComparing(
                          Banner::getPeriod, (p1, p2) -> p2.startDate().compareTo(p1.startDate())))
              .toList();
    };
  }

  private PublishPeriod getPublishPeriod(String startDate, String endDate) {
    return PublishPeriod.builder()
        .startDate(parseDate(startDate))
        .endDate(parseDate(endDate))
        .build();
  }

  private LocalDate parseDate(String value) {
    try {
      return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
    } catch (DateTimeParseException | NullPointerException e) {
      throw new BannerException(INVALID_BANNER_DATE_FORMAT);
    }
  }

  public record BannerCreateOrModifyCommand(
      String location,
      String contentType,
      String publisher,
      String startDate,
      String endDate,
      String link,
      BannerFileStoragePort.UploadFile pcImage,
      BannerFileStoragePort.UploadFile mobileImage) {}

  public record BannerSimple(
      long bannerId,
      String bannerStatus,
      String bannerLocation,
      String bannerType,
      String publisher,
      LocalDate startDate,
      LocalDate endDate) {

    public static BannerSimple from(Banner banner, LocalDate currentDate) {
      return new BannerSimple(
          banner.getId(),
          banner.getPeriod().getPublishStatus(currentDate).getValue(),
          banner.getLocation().getValue(),
          banner.getContentType().getValue(),
          banner.getPublisher(),
          banner.getPeriod().startDate(),
          banner.getPeriod().endDate());
    }
  }

  public record BannerDetail(
      long bannerId,
      String bannerStatus,
      String bannerLocation,
      String bannerType,
      String publisher,
      String link,
      LocalDate startDate,
      LocalDate endDate,
      String pcImageUrl,
      String mobileImageUrl) {

    public static BannerDetail from(
        Banner banner, String pcImageUrl, String mobileImageUrl, LocalDate currentDate) {
      return new BannerDetail(
          banner.getId(),
          banner.getPeriod().getPublishStatus(currentDate).getValue(),
          banner.getLocation().getValue(),
          banner.getContentType().getValue(),
          banner.getPublisher(),
          banner.getLink(),
          banner.getPeriod().startDate(),
          banner.getPeriod().endDate(),
          pcImageUrl,
          mobileImageUrl);
    }
  }

  public record BannerImageWithBothPlatforms(
      String pcUrl, String mobileUrl, LocalDate startDate, LocalDate endDate, String link) {

    public static BannerImageWithBothPlatforms from(
        Banner banner, String pcImageUrl, String mobileImageUrl) {
      return new BannerImageWithBothPlatforms(
          pcImageUrl,
          mobileImageUrl,
          banner.getPeriod().startDate(),
          banner.getPeriod().endDate(),
          banner.getLink());
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

    public BannerPage(List<T> data, Integer totalCount, Integer limit, int currentPage) {
      this(
          limit,
          totalCount,
          (int) Math.ceil((double) totalCount / limit),
          currentPage,
          data,
          (int) Math.ceil((double) totalCount / limit) > currentPage,
          currentPage > 1);
    }
  }

  public enum FilterCriteria {
    ALL("all"),
    RESERVED("reserved"),
    IN_PROGRESS("in_progress"),
    DONE("done");

    private final String parameter;

    FilterCriteria(String parameter) {
      this.parameter = parameter;
    }

    public static FilterCriteria fromParameter(String parameter) {
      return Arrays.stream(values())
          .filter(value -> value.parameter.equals(parameter))
          .findAny()
          .orElseThrow(() -> new BannerException(INVALID_BANNER_PROGRESS_STATUS_PARAMETER));
    }
  }

  public enum SortCriteria {
    START_DATE("start_date"),
    END_DATE("end_date"),
    STATUS("status");

    private final String parameter;

    SortCriteria(String parameter) {
      this.parameter = parameter;
    }

    public static SortCriteria fromParameter(String parameter) {
      return Arrays.stream(values())
          .filter(value -> value.parameter.equals(parameter))
          .findAny()
          .orElseThrow(() -> new BannerException(INVALID_BANNER_SORT_CRITERIA_PARAMETER));
    }
  }
}
