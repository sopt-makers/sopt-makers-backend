package org.sopt.makers.domain.playground.member.ask.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.playground.member.ask.UserAsk;

public interface UserAskRepositoryPort {

  UserAsk save(UserAsk userAsk);

  Optional<UserAsk> findById(Long questionId);

  void deleteById(Long questionId);

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
      Long receiverUserId, LocalDateTime answerCreatedAt, Long questionId);

  long countUnansweredBeforeTargetInLatestOrder(
      Long receiverUserId, LocalDateTime askCreatedAt, Long questionId);

  List<UserAsk> findLatestAnswered(int limit);
}
