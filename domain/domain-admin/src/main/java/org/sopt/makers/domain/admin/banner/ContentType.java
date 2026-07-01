package org.sopt.makers.domain.admin.banner;

import static lombok.AccessLevel.PRIVATE;
import static org.sopt.makers.domain.admin.banner.exception.BannerFailure.NOT_FOUND_CONTENT_TYPE;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.banner.exception.BannerException;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum ContentType {
  PRODUCT("product"),
  BIRTHDAY("birthday"),
  ETC("etc");

  private final String value;

  public static ContentType getByValue(String value) {
    return Arrays.stream(values())
        .filter(contentType -> contentType.value.equals(value))
        .findAny()
        .orElseThrow(() -> new BannerException(NOT_FOUND_CONTENT_TYPE));
  }
}
