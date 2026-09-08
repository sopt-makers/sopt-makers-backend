package org.sopt.makers.domain.playground.member.ask.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.playground.member.ask.UserAsk;

public interface UserAskRepositoryPort {

  UserAsk save(UserAsk userAsk);

  Optional<UserAsk> findById(Long askId);

  void deleteById(Long askId);

  List<Long> findDistinctAnonymousNicknameIdsByReceiverUserId(Long receiverUserId);

  List<UserAsk> findAllByReceiverUserId(Long receiverUserId, int page, int size);

  List<UserAsk> findAnsweredByReceiverUserId(Long receiverUserId, int page, int size);

  List<UserAsk> findUnansweredByReceiverUserId(Long receiverUserId, int page, int size);

  long countAllByReceiverUserId(Long receiverUserId);

  long countAnsweredByReceiverUserId(Long receiverUserId);

  long countUnansweredByReceiverUserId(Long receiverUserId);

  List<UserAsk> findAllAnsweredByAskerUserIdAndReceiverUserIdOrderByLatest(
      Long askerUserId, Long receiverUserId);

  List<Long> findAllAnsweredIdsByReceiverUserIdOrderByLatest(Long receiverUserId);

  long countAnsweredBeforeTargetInLatestOrder(
      Long receiverUserId, LocalDateTime answerCreatedAt, Long askId);

  long countUnansweredBeforeTargetInLatestOrder(
      Long receiverUserId, LocalDateTime askCreatedAt, Long askId);

  List<UserAsk> findLatestAnswered(int limit);

  boolean existsByReceiverUserIdAndCreatedAtAfter(Long receiverUserId, LocalDateTime since);

  /** 각 receiverUserId별 since 이후 생성된 미신고 질문 중 가장 최근 1건씩을 반환한다. */
  List<UserAsk> findLatestRecentByReceiverUserIds(List<Long> receiverUserIds, LocalDateTime since);
}
