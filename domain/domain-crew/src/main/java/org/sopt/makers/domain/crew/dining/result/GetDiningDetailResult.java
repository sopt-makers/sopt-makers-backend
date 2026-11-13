package org.sopt.makers.domain.crew.dining.result;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.sopt.makers.domain.crew.dining.model.enums.DiningPayerStatus;
import org.sopt.makers.domain.crew.meeting.MeetingJoinablePart;

public record GetDiningDetailResult(
    Long meetingId,
    String title,
    LocalDate diningDate,
    boolean isDateModified,
    String imageUrl,
    boolean isEnded,
    MeetingJoinablePart[] joinableParts,
    boolean isJoinablePartsModified,
    DiningLeader leader,
    boolean isLeader,
    boolean isApprovedPayer,
    List<Round> rounds) {

  public record Round(
      Long roundId,
      int roundNumber,
      String place,
      String naverMapLink,
      String kakaoMapLink,
      LocalTime startTime,
      boolean isClosed,
      boolean isPlaceModified,
      boolean isLinkModified,
      boolean isStartTimeModified,
      boolean isNew,
      long attendeeCount,
      MyAttendanceStatus myAttendanceStatus,
      DiningPayerStatus myPayerStatus,
      boolean showNewRoundGuide) {}

  public enum MyAttendanceStatus {
    NOT_ATTENDING,
    ATTENDING,
    ATTENDED
  }
}
