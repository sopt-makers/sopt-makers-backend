package org.sopt.makers.domain.crew.dining.service;

import static org.sopt.makers.domain.crew.dining.exception.DiningFailure.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import org.sopt.makers.domain.crew.dining.exception.DiningException;
import org.sopt.makers.domain.crew.dining.model.DiningMeeting;
import org.sopt.makers.domain.crew.meeting.MeetingImage;
import org.sopt.makers.domain.crew.meeting.MeetingJoinablePart;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** 회식 생성 입력과 날짜·파트 값을 도메인 모델로 변환한다. */
@Component
public class DiningMeetingMapper {

  private static final DateTimeFormatter DINING_DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy.MM.dd");
  private static final DateTimeFormatter START_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("HH:mm");

  public DiningMeeting toDiningMeeting(
      String title,
      List<MeetingImage> imageURL,
      LocalDate diningDate,
      MeetingJoinablePart[] joinableParts,
      Long userId,
      LocalDateTime now,
      Integer activeGeneration) {
    return DiningMeeting.builder()
        .userId(userId)
        .title(title)
        .imageURL(imageURL)
        .startDate(now)
        .endDate(toEndOfDay(diningDate))
        .mStartDate(toStartOfDay(diningDate))
        .mEndDate(toEndOfDay(diningDate))
        .createdGeneration(activeGeneration)
        .joinableParts(joinableParts)
        .build();
  }

  public static LocalDate parseDiningDate(String diningDate) {
    try {
      return LocalDate.parse(diningDate, DINING_DATE_FORMATTER);
    } catch (DateTimeParseException | NullPointerException e) {
      throw new DiningException(VALIDATION_EXCEPTION);
    }
  }

  public static LocalTime parseStartTime(String startTime) {
    if (!StringUtils.hasText(startTime)) {
      return null;
    }
    try {
      return LocalTime.parse(startTime, START_TIME_FORMATTER);
    } catch (DateTimeParseException e) {
      throw new DiningException(VALIDATION_EXCEPTION);
    }
  }

  public static LocalDateTime toStartOfDay(LocalDate date) {
    return date.atStartOfDay();
  }

  public static LocalDateTime toEndOfDay(LocalDate date) {
    return date.atTime(23, 59, 59);
  }

  public static List<MeetingImage> toImageUrls(List<String> files) {
    return IntStream.range(0, files.size())
        .mapToObj(index -> new MeetingImage(index, files.get(index)))
        .toList();
  }

  /** 참여 파트 정규화. null 요소는 거부하고, 중복은 제거해 enum 선언 순서로 정렬한다. */
  public static MeetingJoinablePart[] normalizeJoinableParts(MeetingJoinablePart[] joinableParts) {
    if (joinableParts == null
        || joinableParts.length == 0
        || Arrays.stream(joinableParts).anyMatch(Objects::isNull)) {
      throw new DiningException(VALIDATION_EXCEPTION);
    }
    return Arrays.stream(joinableParts)
        .distinct()
        .sorted(Comparator.comparingInt(Enum::ordinal))
        .toArray(MeetingJoinablePart[]::new);
  }

  /** 빈 문자열로 들어온 선택 입력값(장소, 링크)은 null 로 저장한다. */
  public static String blankToNull(String value) {
    return StringUtils.hasText(value) ? value.trim() : null;
  }
}
