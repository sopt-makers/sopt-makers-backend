package org.sopt.makers.domain.admin.lecture.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.admin.alarm.Alarm;
import org.sopt.makers.domain.admin.alarm.AlarmCategory;
import org.sopt.makers.domain.admin.alarm.AlarmContent;
import org.sopt.makers.domain.admin.alarm.AlarmTarget;
import org.sopt.makers.domain.admin.alarm.port.AlarmInstantSenderPort;
import org.sopt.makers.domain.admin.lecture.Lecture;
import org.sopt.makers.domain.admin.lecture.LectureEndedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class LectureEndedEventListener {

  private static final String ALARM_TITLE_SUFFIX = " 출석점수 반영";
  private static final String ALARM_CONTENT = "출석점수가 새롭게 반영되었어요! 내 점수를 확인해 볼까요?";

  private final AlarmInstantSenderPort alarmInstantSenderPort;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(final LectureEndedEvent event) {
    Lecture lecture = event.lecture();
    try {
      List<String> targetIds = event.userIds().stream().map(String::valueOf).toList();
      AlarmTarget target = AlarmTarget.partialForCsv(lecture.generation(), targetIds);
      AlarmContent content =
          AlarmContent.withoutLink(
              lecture.name() + ALARM_TITLE_SUFFIX, ALARM_CONTENT, AlarmCategory.NOTICE);
      alarmInstantSenderPort.send(Alarm.instant(target, content));
    } catch (Exception e) {
      log.error("출석 종료 알림 발송 실패. lectureId={}", lecture.id(), e);
    }
  }
}
