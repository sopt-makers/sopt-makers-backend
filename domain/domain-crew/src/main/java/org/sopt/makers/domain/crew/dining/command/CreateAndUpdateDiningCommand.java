package org.sopt.makers.domain.crew.dining.command;

import java.util.List;
import org.sopt.makers.domain.crew.meeting.MeetingJoinablePart;

public record CreateAndUpdateDiningCommand(
    String title,
    String place,
    String naverMapLink,
    String kakaoMapLink,
    String diningDate,
    String startTime,
    MeetingJoinablePart[] joinableParts,
    List<String> files) {}
