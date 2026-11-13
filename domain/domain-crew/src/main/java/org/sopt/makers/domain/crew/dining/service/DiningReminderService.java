package org.sopt.makers.domain.crew.dining.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.dining.model.DiningAttendance;
import org.sopt.makers.domain.crew.dining.model.DiningMeeting;
import org.sopt.makers.domain.crew.dining.model.DiningRound;
import org.sopt.makers.domain.crew.dining.notification.DiningCloseGuideTarget;
import org.sopt.makers.domain.crew.dining.notification.DiningReminderTarget;
import org.sopt.makers.domain.crew.dining.port.DiningAttendancePort;
import org.sopt.makers.domain.crew.dining.port.DiningMeetingPort;
import org.sopt.makers.domain.crew.dining.port.DiningRoundPort;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 회식 알림 스케줄러의 발송 대상 조회. 발송은 조회 트랜잭션 밖에서 한다. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiningReminderService {

  private final DiningMeetingPort meetingRepository;
  private final DiningRoundPort diningRoundRepository;
  private final DiningAttendancePort diningAttendanceRepository;
  private final Clock clock;

  /** 오늘이 회식 일자이고, 종료되지 않았으며, 마감하지 않은 회차가 있는 회식의 개설자. */
  public List<DiningCloseGuideTarget> findCloseGuideTargets() {
    List<DiningMeeting> dinings =
        findOngoingDiningsOn(
            java.time.LocalDateTime.now(clock.withZone(java.time.ZoneId.of("Asia/Seoul")))
                .toLocalDate());
    if (dinings.isEmpty()) {
      return List.of();
    }
    Set<Long> diningIdsWithOpenRound =
        diningRoundRepository
            .findAllByMeetingIdInOrderByIdAsc(dinings.stream().map(DiningMeeting::getId).toList())
            .stream()
            .filter(round -> !round.isClosed())
            .map(DiningRound::getMeetingId)
            .collect(Collectors.toSet());
    return dinings.stream()
        .filter(dining -> diningIdsWithOpenRound.contains(dining.getId()))
        .map(
            dining ->
                new DiningCloseGuideTarget(dining.getId(), dining.getTitle(), dining.getUserId()))
        .toList();
  }

  /** 내일이 회식 일자이고 종료되지 않은 회식의 전체 회차 참석자. 실행 시점(전날 16:00)에 있는 회식만 대상이다. */
  public List<DiningReminderTarget> findReminderTargets() {
    List<DiningMeeting> dinings =
        findOngoingDiningsOn(
            java.time.LocalDateTime.now(clock.withZone(java.time.ZoneId.of("Asia/Seoul")))
                .toLocalDate()
                .plusDays(1));
    if (dinings.isEmpty()) {
      return List.of();
    }
    Map<Long, Long> meetingIdByRoundId =
        diningRoundRepository
            .findAllByMeetingIdInOrderByIdAsc(dinings.stream().map(DiningMeeting::getId).toList())
            .stream()
            .collect(Collectors.toMap(DiningRound::getId, DiningRound::getMeetingId));
    Map<Long, List<Long>> attendeesByMeetingId =
        meetingIdByRoundId.isEmpty()
            ? Map.of()
            : diningAttendanceRepository
                .findAllByRoundIdIn(List.copyOf(meetingIdByRoundId.keySet()))
                .stream()
                .collect(
                    Collectors.groupingBy(
                        attendance -> meetingIdByRoundId.get(attendance.getRoundId()),
                        Collectors.mapping(
                            DiningAttendance::getUserId,
                            Collectors.collectingAndThen(
                                Collectors.toList(), ids -> ids.stream().distinct().toList()))));
    return dinings.stream()
        .filter(dining -> attendeesByMeetingId.containsKey(dining.getId()))
        .map(
            dining ->
                new DiningReminderTarget(
                    dining.getId(), dining.getTitle(), attendeesByMeetingId.get(dining.getId())))
        .toList();
  }

  private List<DiningMeeting> findOngoingDiningsOn(LocalDate date) {
    return meetingRepository.findOngoingDiningsByDateBetween(
        MeetingCategory.DINING, date.atStartOfDay(), date.plusDays(1).atStartOfDay());
  }
}
