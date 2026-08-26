package org.sopt.makers.domain.app.soptamp.mission;

import java.util.List;

public record Mission(
    Long id, String title, Integer level, boolean display, List<String> profileImages) {

  public Mission {
    profileImages = profileImages == null ? List.of() : List.copyOf(profileImages);
  }
}
