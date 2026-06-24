package org.sopt.makers.clients.scraper.crawler;

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

  public static LinkSource parseSource(String link) {
    return Arrays.stream(values())
        .filter(v -> link.startsWith(v.prefix) && link.endsWith(v.suffix))
        .findAny()
        .orElse(BASIC);
  }
}
