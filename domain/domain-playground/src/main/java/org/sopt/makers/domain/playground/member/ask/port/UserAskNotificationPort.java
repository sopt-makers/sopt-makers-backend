package org.sopt.makers.domain.playground.member.ask.port;

public interface UserAskNotificationPort {

  void sendAskNotification(Long askId, Long receiverId, String content);

  void sendAnswerNotification(Long askId, Long askerId, Long answerWriterId, String answerContent);
}
