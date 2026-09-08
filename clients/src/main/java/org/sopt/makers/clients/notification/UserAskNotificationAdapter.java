package org.sopt.makers.clients.notification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.admin.alarm.Alarm;
import org.sopt.makers.domain.admin.alarm.AlarmCategory;
import org.sopt.makers.domain.admin.alarm.AlarmContent;
import org.sopt.makers.domain.admin.alarm.AlarmTarget;
import org.sopt.makers.domain.admin.alarm.port.AlarmInstantSenderPort;
import org.sopt.makers.domain.auth.port.SmsSenderPort;
import org.sopt.makers.domain.playground.member.ask.port.CurrentGenerationProvider;
import org.sopt.makers.domain.playground.member.ask.port.UserAskNotificationPort;
import org.sopt.makers.domain.user.port.PlaygroundAskUserPort;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAskNotificationAdapter implements UserAskNotificationPort {

  private static final String ASK_PROFILE_LINK_FORMAT =
      "https://playground.sopt.org/members/%d?tab=ask";
  private static final String QUESTION_NOTIFICATION_TITLE = "💬나의 에스크에 질문이 달렸어요.";
  private static final String QUESTION_CONTENT_FORMAT = "[이런 내용이 궁금해요] : \"%s\"";
  private static final String ANSWER_NOTIFICATION_TITLE = "💬나의 에스크에 답변이 달렸어요.";
  private static final String ANSWER_CONTENT_FORMAT = "[%s의 댓글] : \"%s\"";
  private static final int CONTENT_MAX_LENGTH = 100;

  private final AlarmInstantSenderPort alarmInstantSenderPort;
  private final SmsSenderPort smsSenderPort;
  private final PlaygroundAskUserPort playgroundAskUserPort;
  private final CurrentGenerationProvider currentGenerationProvider;

  @Override
  public void sendQuestionNotification(Long askId, Long receiverId, String questionContent) {
    try {
      int lastGeneration = playgroundAskUserPort.getLastSoptGeneration(receiverId);
      if (lastGeneration == currentGenerationProvider.getCurrentGeneration()) {
        alarmInstantSenderPort.send(buildQuestionAlarm(receiverId, questionContent));
      } else {
        String phoneNumber = playgroundAskUserPort.getPhoneNumber(receiverId);
        smsSenderPort.send(phoneNumber, buildQuestionSmsMessage(receiverId, questionContent));
      }
    } catch (Exception e) {
      log.error("질문 알림 발송 실패: askId={}, receiverId={}", askId, receiverId, e);
    }
  }

  @Override
  public void sendAnswerNotification(
      Long askId, Long askerId, Long answerWriterId, String answerContent) {
    try {
      String answerWriterName = playgroundAskUserPort.getName(answerWriterId);
      alarmInstantSenderPort.send(buildAnswerAlarm(askerId, answerWriterName, answerContent));
    } catch (Exception e) {
      log.error("답변 알림 발송 실패: askId={}, askerId={}", askId, askerId, e);
    }
  }

  private Alarm buildQuestionAlarm(Long receiverId, String questionContent) {
    AlarmTarget target =
        AlarmTarget.partialForCsv(
            currentGenerationProvider.getCurrentGeneration(), List.of(String.valueOf(receiverId)));
    AlarmContent content =
        AlarmContent.withoutLink(
            QUESTION_NOTIFICATION_TITLE,
            String.format(QUESTION_CONTENT_FORMAT, abbreviate(questionContent)),
            AlarmCategory.NEWS);
    return Alarm.instant(target, content);
  }

  private Alarm buildAnswerAlarm(Long askerId, String answerWriterName, String answerContent) {
    AlarmTarget target =
        AlarmTarget.partialForCsv(
            currentGenerationProvider.getCurrentGeneration(), List.of(String.valueOf(askerId)));
    AlarmContent content =
        AlarmContent.withoutLink(
            ANSWER_NOTIFICATION_TITLE,
            String.format(ANSWER_CONTENT_FORMAT, answerWriterName, abbreviate(answerContent)),
            AlarmCategory.NEWS);
    return Alarm.instant(target, content);
  }

  private String buildQuestionSmsMessage(Long receiverId, String questionContent) {
    return "[SOPT makers] 내 에스크에 질문이 달렸어요!\n\n"
        + "- [이런 내용이 궁금해요] "
        + questionContent
        + "\n"
        + "- [답변하러 가기] "
        + String.format(ASK_PROFILE_LINK_FORMAT, receiverId);
  }

  private String abbreviate(String content) {
    if (content == null || content.length() <= CONTENT_MAX_LENGTH) {
      return content;
    }
    return content.substring(0, CONTENT_MAX_LENGTH - 3) + "...";
  }
}
