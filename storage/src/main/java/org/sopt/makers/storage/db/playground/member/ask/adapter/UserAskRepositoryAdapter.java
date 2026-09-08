package org.sopt.makers.storage.db.playground.member.ask.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.ask.UserAsk;
import org.sopt.makers.domain.playground.member.ask.port.UserAskRepositoryPort;
import org.sopt.makers.storage.db.playground.member.ask.entity.UserAskEntity;
import org.sopt.makers.storage.db.playground.member.ask.repository.UserAskJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAskRepositoryAdapter implements UserAskRepositoryPort {

  private final UserAskJpaRepository userAskJpaRepository;

  @Transactional
  @Override
  public UserAsk save(UserAsk userAsk) {
    return userAskJpaRepository.save(UserAskEntity.fromDomain(userAsk)).toDomain();
  }

  @Override
  public Optional<UserAsk> findById(Long questionId) {
    return userAskJpaRepository.findById(questionId).map(UserAskEntity::toDomain);
  }

  @Transactional
  @Override
  public void deleteById(Long questionId) {
    userAskJpaRepository.deleteById(questionId);
  }

  @Override
  public List<Long> findDistinctAnonymousNicknameIdsByReceiverUserId(Long receiverUserId) {
    return userAskJpaRepository.findDistinctAnonymousNicknameIdsByReceiverUserId(receiverUserId);
  }

  @Override
  public List<UserAsk> findAllByReceiverUserId(Long receiverUserId, int page, int size) {
    return toDomainList(
        userAskJpaRepository.findAllByReceiverUserId(receiverUserId, PageRequest.of(page, size)));
  }

  @Override
  public List<UserAsk> findAnsweredByReceiverUserId(Long receiverUserId, int page, int size) {
    return toDomainList(
        userAskJpaRepository.findAnsweredByReceiverUserId(
            receiverUserId, PageRequest.of(page, size)));
  }

  @Override
  public List<UserAsk> findUnansweredByReceiverUserId(Long receiverUserId, int page, int size) {
    return toDomainList(
        userAskJpaRepository.findUnansweredByReceiverUserId(
            receiverUserId, PageRequest.of(page, size)));
  }

  @Override
  public long countAllByReceiverUserId(Long receiverUserId) {
    return userAskJpaRepository.countByReceiverUserId(receiverUserId);
  }

  @Override
  public long countAnsweredByReceiverUserId(Long receiverUserId) {
    return userAskJpaRepository.countAnsweredByReceiverUserId(receiverUserId);
  }

  @Override
  public long countUnansweredByReceiverUserId(Long receiverUserId) {
    return userAskJpaRepository.countUnansweredByReceiverUserId(receiverUserId);
  }

  @Override
  public List<UserAsk> findAllAnsweredByAskerUserIdAndReceiverUserIdOrderByLatest(
      Long askerUserId, Long receiverUserId) {
    return toDomainList(
        userAskJpaRepository.findAllAnsweredByAskerUserIdAndReceiverUserIdOrderByLatest(
            askerUserId, receiverUserId));
  }

  @Override
  public List<Long> findAllAnsweredIdsByReceiverUserIdOrderByLatest(Long receiverUserId) {
    return userAskJpaRepository.findAllAnsweredIdsByReceiverUserIdOrderByLatest(receiverUserId);
  }

  @Override
  public long countAnsweredBeforeTargetInLatestOrder(
      Long receiverUserId, LocalDateTime answerCreatedAt, Long questionId) {
    return userAskJpaRepository.countAnsweredBeforeTargetInLatestOrder(
        receiverUserId, answerCreatedAt, questionId);
  }

  @Override
  public long countUnansweredBeforeTargetInLatestOrder(
      Long receiverUserId, LocalDateTime askCreatedAt, Long questionId) {
    return userAskJpaRepository.countUnansweredBeforeTargetInLatestOrder(
        receiverUserId, askCreatedAt, questionId);
  }

  @Override
  public List<UserAsk> findLatestAnswered(int limit) {
    return toDomainList(userAskJpaRepository.findLatestAnswered(PageRequest.of(0, limit)));
  }

  @Override
  public boolean existsByReceiverUserIdAndCreatedAtAfter(Long receiverUserId, LocalDateTime since) {
    return userAskJpaRepository.existsByReceiverUserIdAndCreatedAtAfter(receiverUserId, since);
  }

  @Override
  public List<UserAsk> findLatestRecentByReceiverUserIds(
      List<Long> receiverUserIds, LocalDateTime since) {
    if (receiverUserIds == null || receiverUserIds.isEmpty()) {
      return List.of();
    }
    return toDomainList(
        userAskJpaRepository.findLatestRecentByReceiverUserIds(receiverUserIds, since));
  }

  private List<UserAsk> toDomainList(List<UserAskEntity> entities) {
    return entities.stream().map(UserAskEntity::toDomain).toList();
  }
}
