package org.sopt.makers.domain.app.soptletter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SoptLetterColor {
  BLUE_50("#C8E1FF"),
  YELLOW_50("#FFF4D4"),
  GREEN_50("#CCFFEC"),
  RED_50("#FFD1D3");

  private final String hexCode;

  public SoptLetterColor next() {
    return switch (this) {
      case BLUE_50 -> GREEN_50;
      case GREEN_50 -> YELLOW_50;
      case YELLOW_50 -> RED_50;
      case RED_50 -> BLUE_50;
    };
  }

  public static SoptLetterColor first() {
    return BLUE_50;
  }
}
