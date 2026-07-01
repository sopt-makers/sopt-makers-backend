package org.sopt.makers.domain.admin.banner;

import static org.sopt.makers.domain.admin.banner.exception.BannerFailure.INVALID_BANNER_PERIOD;

import java.time.LocalDate;
import lombok.Builder;
import org.sopt.makers.domain.admin.banner.exception.BannerException;

public record PublishPeriod(LocalDate startDate, LocalDate endDate) {

  @Builder
  public PublishPeriod {
    if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
      throw new BannerException(INVALID_BANNER_PERIOD);
    }
  }

  public PublishStatus getPublishStatus(LocalDate date) {
    if (date.isAfter(endDate)) {
      return PublishStatus.DONE;
    }
    if (date.isBefore(startDate)) {
      return PublishStatus.RESERVED;
    }
    return PublishStatus.IN_PROGRESS;
  }
}
