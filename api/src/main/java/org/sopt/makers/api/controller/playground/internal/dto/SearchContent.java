package org.sopt.makers.api.controller.playground.internal.dto;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SearchContent {
  UNIVERSITY("university"),
  MBTI("mbti");

  private final String key;
  private static final Map<String, SearchContent> KEY_MAP = new HashMap<>();

  static {
    for (SearchContent content : values()) {
      KEY_MAP.put(content.key, content);
    }
  }

  public static SearchContent of(String key) {
    SearchContent result = KEY_MAP.get(key.toLowerCase());
    if (result == null) {
      throw new IllegalArgumentException("Unknown key: " + key);
    }
    return result;
  }
}
