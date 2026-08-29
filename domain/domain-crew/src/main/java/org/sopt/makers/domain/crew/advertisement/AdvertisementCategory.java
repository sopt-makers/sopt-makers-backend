package org.sopt.makers.domain.crew.advertisement;

public enum AdvertisementCategory {
  POST(6, true),
  MEETING(1, true),
  MEETING_TOP(1, false);

  private final int maxItems;
  private final boolean generalAdvertisement;

  AdvertisementCategory(int maxItems, boolean generalAdvertisement) {
    this.maxItems = maxItems;
    this.generalAdvertisement = generalAdvertisement;
  }

  public int getMaxItems() {
    return maxItems;
  }

  public boolean isGeneralAdvertisement() {
    return generalAdvertisement;
  }
}
