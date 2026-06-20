package org.sopt.makers.domain.user;

public record UserFavor(
    Boolean isPourSauceLover,
    Boolean isHardPeachLover,
    Boolean isMintChocoLover,
    Boolean isRedBeanFishBreadLover,
    Boolean isSojuLover,
    Boolean isRiceTteokLover) {

  public static UserFavor of(
      Boolean isPourSauceLover,
      Boolean isHardPeachLover,
      Boolean isMintChocoLover,
      Boolean isRedBeanFishBreadLover,
      Boolean isSojuLover,
      Boolean isRiceTteokLover) {
    return new UserFavor(
        isPourSauceLover,
        isHardPeachLover,
        isMintChocoLover,
        isRedBeanFishBreadLover,
        isSojuLover,
        isRiceTteokLover);
  }
}
