package org.sopt.makers.api.controller.crew.meeting.dto;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.domain.crew.meeting.MeetingFrequency;
import org.sopt.makers.domain.crew.meeting.MeetingImage;
import org.sopt.makers.domain.crew.meeting.MeetingJoinInfo;
import org.sopt.makers.domain.crew.meeting.MeetingJoinablePart;
import org.sopt.makers.domain.crew.meeting.MeetingType;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;

@NoArgsConstructor(access = PRIVATE)
public final class MeetingRequest {

  private static final DateTimeFormatter LEGACY_DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy.MM.dd");

  public record Create(
      Long meetingDemandId,
      @NotBlank(message = "모임 제목은 필수 입력 값입니다.") String title,
      @NotBlank(message = "모임 부제목은 필수 입력 값입니다.") @Size(max = 30) String subTitle,
      @NotEmpty(message = "모임 이미지는 필수 입력 값입니다.") @Size(max = 6) List<String> files,
      @NotBlank(message = "모임 카테고리는 필수 입력 값입니다.") String category,
      @NotBlank(message = "모집 시작일은 필수 입력 값입니다.") String startDate,
      @NotBlank(message = "모집 종료일은 필수 입력 값입니다.") String endDate,
      @NotNull(message = "모집 인원은 필수 입력 값입니다.") Integer capacity,
      @NotBlank(message = "모임 설명은 필수 입력 값입니다.") String desc,
      String processDesc,
      String mStartDate,
      String mEndDate,
      String leaderDesc,
      String note,
      @NotNull(message = "멘토 필요 여부는 필수 입력 값입니다.") Boolean isMentorNeeded,
      @NotNull(message = "활동 기수 제한 여부는 필수 입력 값입니다.") Boolean canJoinOnlyActiveGeneration,
      @NotNull(message = "참여 정보는 필수 입력 값입니다.") JoinInfo joinInfo,
      Integer createdGeneration,
      Integer targetActiveGeneration,
      @NotEmpty(message = "참여 가능 파트는 필수 입력 값입니다.") @Size(max = 6) List<String> joinableParts,
      List<Long> coLeaderUserIds) {

    public MeetingService.CreateMeetingCommand toCommand() {
      return new MeetingService.CreateMeetingCommand(
          meetingDemandId,
          title,
          subTitle,
          MeetingCategory.ofValue(category),
          toImages(files),
          parseStart(startDate),
          parseEnd(endDate),
          capacity,
          desc,
          processDesc,
          parseStart(mStartDate),
          parseEnd(mEndDate),
          leaderDesc,
          note,
          isMentorNeeded,
          canJoinOnlyActiveGeneration,
          joinInfo.toDomain(),
          createdGeneration,
          targetActiveGeneration,
          toJoinableParts(joinableParts),
          coLeaderUserIds);
    }
  }

  public record Update(
      String title,
      @Size(max = 30) String subTitle,
      List<String> files,
      String category,
      String startDate,
      String endDate,
      Integer capacity,
      String desc,
      String processDesc,
      String mStartDate,
      String mEndDate,
      String leaderDesc,
      String note,
      Boolean isMentorNeeded,
      Boolean canJoinOnlyActiveGeneration,
      JoinInfo joinInfo,
      Integer targetActiveGeneration,
      List<String> joinableParts,
      List<Long> coLeaderUserIds) {

    public MeetingService.UpdateMeetingCommand toCommand() {
      return new MeetingService.UpdateMeetingCommand(
          title,
          subTitle,
          category == null ? null : MeetingCategory.ofValue(category),
          files == null ? null : toImages(files),
          parseStart(startDate),
          parseEnd(endDate),
          capacity,
          desc,
          processDesc,
          parseStart(mStartDate),
          parseEnd(mEndDate),
          leaderDesc,
          note,
          isMentorNeeded,
          canJoinOnlyActiveGeneration,
          joinInfo == null ? null : joinInfo.toDomain(),
          targetActiveGeneration,
          joinableParts == null ? null : toJoinableParts(joinableParts),
          coLeaderUserIds);
    }
  }

  public record Apply(@NotNull(message = "모임 ID는 필수 입력 값입니다.") Long meetingId, String content) {

    public MeetingService.ApplyMeetingCommand toCommand() {
      return new MeetingService.ApplyMeetingCommand(meetingId, content);
    }
  }

  public record UpdateApplyStatus(
      @NotNull(message = "신청 ID는 필수 입력 값입니다.") Long applyId,
      @NotNull(message = "신청 상태는 필수 입력 값입니다.") Integer status) {}

  public record JoinInfo(String meetingType, String meetingFrequency) {

    public MeetingJoinInfo toDomain() {
      return new MeetingJoinInfo(
          MeetingType.ofValue(meetingType), MeetingFrequency.ofValue(meetingFrequency));
    }
  }

  private static List<MeetingImage> toImages(List<String> files) {
    AtomicInteger index = new AtomicInteger();
    return files.stream().map(url -> new MeetingImage(index.getAndIncrement(), url)).toList();
  }

  private static List<MeetingJoinablePart> toJoinableParts(List<String> values) {
    return values.stream().map(String::toUpperCase).map(MeetingJoinablePart::valueOf).toList();
  }

  private static LocalDateTime parseStart(String value) {
    return parseDate(value, true);
  }

  private static LocalDateTime parseEnd(String value) {
    return parseDate(value, false);
  }

  private static LocalDateTime parseDate(String value, boolean startOfDay) {
    if (value == null || value.isBlank()) {
      return null;
    }
    if (value.contains("T")) {
      return LocalDateTime.parse(value);
    }
    LocalDate date = LocalDate.parse(value, LEGACY_DATE_FORMATTER);
    return startOfDay ? date.atStartOfDay() : date.atTime(23, 59, 59);
  }
}
