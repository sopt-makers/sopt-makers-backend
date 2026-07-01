package org.sopt.makers.domain.admin.banner;

import static lombok.AccessLevel.PRIVATE;
import static org.sopt.makers.domain.admin.banner.exception.BannerFailure.NOT_FOUND_STATUS;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.banner.exception.BannerException;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum PublishStatus {
  RESERVED("reserved"),
  IN_PROGRESS("in_progress"),
  DONE("done");

  private final String value;

  public static PublishStatus getByValue(String value) {
    return Arrays.stream(values())
        .filter(status -> status.value.equals(value))
        .findAny()
        .orElseThrow(() -> new BannerException(NOT_FOUND_STATUS));
  }
}
