package org.sopt.makers.api.controller.crew.meeting.dto;

import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.INVALID_APPLY_STATUS;
import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.INVALID_MEETING_VALUE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.sopt.makers.domain.crew.meeting.MeetingApplyStatus;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.domain.crew.meeting.MeetingFrequency;
import org.sopt.makers.domain.crew.meeting.MeetingImage;
import org.sopt.makers.domain.crew.meeting.MeetingJoinInfo;
import org.sopt.makers.domain.crew.meeting.MeetingJoinablePart;
import org.sopt.makers.domain.crew.meeting.MeetingType;
import org.sopt.makers.domain.crew.meeting.exception.MeetingException;

public interface MeetingMapper {

  DateTimeFormatter LEGACY_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

  static MeetingCategory getCategory(String category) {
    if (category == null) {
      return null;
    }
    return MeetingCategory.ofValue(category);
  }

  static List<MeetingImage> getImageURL(List<String> files) {
    if (files == null) {
      return null;
    }
    return IntStream.range(0, files.size())
        .mapToObj(index -> new MeetingImage(index, files.get(index)))
        .toList();
  }

  static MeetingJoinInfo getJoinInfo(MeetingJoinInfoRequest joinInfo) {
    if (joinInfo == null) {
      return null;
    }
    return new MeetingJoinInfo(
        MeetingType.ofValue(joinInfo.meetingType()),
        MeetingFrequency.ofValue(joinInfo.meetingFrequency()));
  }

  static List<MeetingJoinablePart> getJoinableParts(List<String> values) {
    if (values == null) {
      return null;
    }
    return values.stream().map(MeetingMapper::getJoinablePart).toList();
  }

  static MeetingJoinablePart getJoinablePart(String value) {
    if (value == null || value.isBlank()) {
      throw new MeetingException(INVALID_MEETING_VALUE);
    }
    return Arrays.stream(MeetingJoinablePart.values())
        .filter(
            part ->
                part.name().equalsIgnoreCase(value.trim())
                    || part.getDisplayName().equals(value.trim()))
        .findFirst()
        .orElseThrow(() -> new MeetingException(INVALID_MEETING_VALUE));
  }

  static MeetingApplyStatus getApplyStatus(Integer status) {
    if (status == null) {
      throw new MeetingException(INVALID_APPLY_STATUS);
    }
    return MeetingApplyStatus.ofValue(status);
  }

  static LocalDateTime getStartDate(String date) {
    return getDate(date, true);
  }

  static LocalDateTime getEndDate(String date) {
    return getDate(date, false);
  }

  private static LocalDateTime getDate(String date, boolean startOfDay) {
    if (date == null || date.isBlank()) {
      return null;
    }
    try {
      if (date.contains("T")) {
        return LocalDateTime.parse(date);
      }
      LocalDate parsedDate = LocalDate.parse(date, LEGACY_DATE_FORMATTER);
      return startOfDay ? parsedDate.atStartOfDay() : parsedDate.atTime(23, 59, 59);
    } catch (DateTimeParseException e) {
      throw new MeetingException(INVALID_MEETING_VALUE);
    }
  }
}
