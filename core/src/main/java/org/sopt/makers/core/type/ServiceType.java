package org.sopt.makers.core.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServiceType {
  AUTH("auth"),
  OFFICIAL("official"),
  ADMIN("admin"),
  APP("app"),
  PLAYGROUND("playground"),
  CREW("crew");

  private final String value;
}
