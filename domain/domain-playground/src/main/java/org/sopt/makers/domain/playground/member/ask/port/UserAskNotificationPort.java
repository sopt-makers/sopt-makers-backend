package org.sopt.makers.domain.playground.member.ask.port;

public interface UserAskNotificationPort {

  void sendQuestionNotification(Long askId, Long receiverId, String questionContent);

  void sendAnswerNotification(Long askId, Long askerId, Long answerWriterId, String answerContent);
}
