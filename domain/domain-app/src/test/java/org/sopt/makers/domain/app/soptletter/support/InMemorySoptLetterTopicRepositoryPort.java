package org.sopt.makers.domain.app.soptletter.support;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;
import org.sopt.makers.domain.app.soptletter.port.SoptLetterTopicRepositoryPort;

public final class InMemorySoptLetterTopicRepositoryPort implements SoptLetterTopicRepositoryPort {

  private final List<SoptLetterTopic> store = new ArrayList<>();

  public SoptLetterTopic add(SoptLetterTopic topic) {
    store.add(topic);
    return topic;
  }

  @Override
  public Optional<SoptLetterTopic> findById(Long topicId) {
    return store.stream().filter(t -> t.id().equals(topicId)).findFirst();
  }

  @Override
  public List<SoptLetterTopic> findActiveTopicsDefaultFirst(LocalDateTime now) {
    return store.stream()
        .filter(t -> t.isActiveAt(now))
        .sorted(
            Comparator.comparing(SoptLetterTopic::isDefault)
                .reversed()
                .thenComparing(
                    SoptLetterTopic::createdAt,
                    Comparator.nullsLast(Comparator.<LocalDateTime>reverseOrder())))
        .toList();
  }

  @Override
  public List<SoptLetterTopic> findActiveDefaultTopicsLatestFirst(LocalDateTime now) {
    return latestFirst(store.stream().filter(t -> t.isDefault() && t.isActiveAt(now)).toList());
  }

  @Override
  public List<SoptLetterTopic> findActiveNormalTopicsLatestFirst(LocalDateTime now) {
    return latestFirst(store.stream().filter(t -> !t.isDefault() && t.isActiveAt(now)).toList());
  }

  @Override
  public List<SoptLetterTopic> findDefaultTopicsLatestFirst() {
    return latestFirst(store.stream().filter(SoptLetterTopic::isDefault).toList());
  }

  @Override
  public boolean existsNormalTopic() {
    return store.stream().anyMatch(t -> !t.isDefault());
  }

  @Override
  public List<SoptLetterTopic> findActiveCtasLatestFirst(LocalDateTime now) {
    return store.stream()
        .filter(t -> t.ctaText() != null && t.isActiveAt(now))
        .sorted(
            Comparator.comparing(SoptLetterTopic::startedAt)
                .thenComparing(SoptLetterTopic::id)
                .reversed())
        .toList();
  }

  private List<SoptLetterTopic> latestFirst(List<SoptLetterTopic> topics) {
    return topics.stream()
        .sorted(Comparator.comparing(SoptLetterTopic::createdAt).reversed())
        .toList();
  }
}
