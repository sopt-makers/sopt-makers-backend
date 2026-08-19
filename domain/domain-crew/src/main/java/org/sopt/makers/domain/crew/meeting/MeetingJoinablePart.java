package org.sopt.makers.domain.crew.meeting;

public enum MeetingJoinablePart {
  PM("기획"),
  DESIGN("디자인"),
  IOS("iOS"),
  ANDROID("안드로이드"),
  SERVER("서버"),
  WEB("웹");

  private final String displayName;

  MeetingJoinablePart(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
