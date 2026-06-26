package org.sopt.makers.clients.scrap;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LinkSource {
  NAVER("https://blog.naver.com/", ""),
  BRUNCH("https://brunch.co.kr/", ""),
  VELOG_MAIN("https://velog.io/", "/posts"),
  MEDIUM("https://medium.com/sopt-makers/", ""),
  BASIC("basic", "");

  private final String prefix;
  private final String suffix;

  static LinkSource parseSource(String link) {
    return Arrays.stream(values())
        .filter(value -> link.startsWith(value.prefix) && link.endsWith(value.suffix))
        .findAny()
        .orElse(BASIC);
  }
}
