package org.sopt.makers.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Team {
  MAKERS("메이커스"),
  MEDIA("미디어팀"),
  OPERATION("운영팀");

  private final String displayName;
}
