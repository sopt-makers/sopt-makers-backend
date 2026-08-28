package org.sopt.makers.domain.crew.advertisement;

public enum AdvertisementEventType {
  SOPKATHON("솝커톤"),
  NETWORKING("네트워킹");

  private final String displayName;

  AdvertisementEventType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
