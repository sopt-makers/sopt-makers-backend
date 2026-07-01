package org.sopt.makers.domain.admin.banner;

import static lombok.AccessLevel.PRIVATE;
import static org.sopt.makers.domain.admin.banner.exception.BannerFailure.NOT_FOUND_LOCATION;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.banner.exception.BannerException;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum PublishLocation {
  PLAYGROUND_COMMUNITY("pg_community"),
  CREW_MAIN("cr_main"),
  CREW_FEED("cr_feed");

  private final String value;

  public static PublishLocation getByValue(String value) {
    return Arrays.stream(values())
        .filter(location -> location.value.equals(value))
        .findAny()
        .orElseThrow(() -> new BannerException(NOT_FOUND_LOCATION));
  }
}
