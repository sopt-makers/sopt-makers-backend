package org.sopt.makers.domain.crew.meeting.demand;

public enum MeetingDemandStatus {
  BEFORE_OPEN("개설전"),
  OPENED("개설완료");

  private final String value;

  MeetingDemandStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
