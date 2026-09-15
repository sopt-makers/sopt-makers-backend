package org.sopt.makers.domain.app.home;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AppServiceName {
  POKE("콕찌르기", "POKE", "pokeBadgeManager"),
  SOPTAMP("솝탬프", "SOPTAMP", "soptampBadgeManager"),
  SOPT_LETTER("솝레터", "SOPT_LETTER", "defaultBadgeManager"),
  OTHERS("", "OTHERS", "defaultBadgeManager"),
  FLOATING_BUTTON("FAB", "FLOATING_BUTTON", "floatingButtonBadgeManager"),
  REVIEW_FORM("후기폼", "REVIEW_FORM", "reviewFormBadgeManager");

  private final String exposedName;
  private final String serviceName;
  private final String badgeManagerName;

  public static AppServiceName of(String serviceName) {
    return Arrays.stream(values())
        .filter(name -> name.serviceName.equals(serviceName))
        .findFirst()
        .orElse(OTHERS);
  }
}
