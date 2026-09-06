package org.sopt.makers.domain.app.home;

import java.util.List;

public record MainView(
    ActivityStatus status,
    String name,
    String profileImage,
    String part,
    List<Long> generationList,
    boolean isAllConfirm) {

  public static MainView unauthenticated() {
    return new MainView(ActivityStatus.UNAUTHENTICATED, "", "", "", List.of(), false);
  }
}
