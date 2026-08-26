package org.sopt.makers.domain.app.soptletter.support;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.soptletter.SoptLetter;
import org.sopt.makers.domain.app.soptletter.port.SoptLetterRepositoryPort;

public final class InMemorySoptLetterRepositoryPort implements SoptLetterRepositoryPort {

  private final List<SoptLetter> store = new ArrayList<>();
  private long sequence = 1L;
  private LocalDateTime createdAt = LocalDateTime.of(2026, 8, 24, 12, 0);

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public Optional<SoptLetter> findById(Long letterId) {
    return store.stream().filter(l -> l.id().equals(letterId)).findFirst();
  }

  @Override
  public List<SoptLetter> findPageByTopicId(Long topicId, Long cursor, int size) {
    return store.stream()
        .filter(l -> l.topicId().equals(topicId))
        .filter(l -> cursor == null || l.id() < cursor)
        .sorted(Comparator.comparingLong(SoptLetter::id).reversed())
        .limit(size)
        .toList();
  }

  @Override
  public Optional<SoptLetter> findLatestByTopicId(Long topicId) {
    return store.stream()
        .filter(l -> l.topicId().equals(topicId))
        .max(Comparator.comparingLong(SoptLetter::id));
  }

  @Override
  public long countByTopicId(Long topicId) {
    return store.stream().filter(l -> l.topicId().equals(topicId)).count();
  }

  @Override
  public long countByAuthorProfileIdSince(Long authorProfileId, LocalDateTime since) {
    return store.stream()
        .filter(l -> l.authorProfileId().equals(authorProfileId))
        .filter(l -> l.createdAt() != null && !l.createdAt().isBefore(since))
        .count();
  }

  @Override
  public boolean existsByIdAndTopicId(Long letterId, Long topicId) {
    return findById(letterId).filter(l -> l.topicId().equals(topicId)).isPresent();
  }

  @Override
  public SoptLetter save(SoptLetter soptLetter) {
    SoptLetter saved =
        new SoptLetter(
            sequence++,
            soptLetter.authorProfileId(),
            soptLetter.topicId(),
            soptLetter.degree(),
            soptLetter.message(),
            soptLetter.color(),
            soptLetter.shapeType(),
            soptLetter.likeCount(),
            createdAt,
            createdAt);
    store.add(saved);
    return saved;
  }

  @Override
  public void updateMessage(Long letterId, String message) {
    replace(letterId, letter -> withMessage(letter, message));
  }

  @Override
  public void deleteById(Long letterId) {
    store.removeIf(l -> l.id().equals(letterId));
  }

  @Override
  public void increaseLikeCount(Long letterId) {
    replace(letterId, letter -> withLikeCount(letter, letter.likeCount() + 1));
  }

  @Override
  public void decreaseLikeCount(Long letterId) {
    replace(letterId, letter -> withLikeCount(letter, Math.max(0, letter.likeCount() - 1)));
  }

  private void replace(Long letterId, java.util.function.UnaryOperator<SoptLetter> operator) {
    findById(letterId)
        .ifPresent(letter -> store.set(store.indexOf(letter), operator.apply(letter)));
  }

  private SoptLetter withMessage(SoptLetter letter, String message) {
    return new SoptLetter(
        letter.id(),
        letter.authorProfileId(),
        letter.topicId(),
        letter.degree(),
        message,
        letter.color(),
        letter.shapeType(),
        letter.likeCount(),
        letter.createdAt(),
        letter.updatedAt());
  }

  private SoptLetter withLikeCount(SoptLetter letter, int likeCount) {
    return new SoptLetter(
        letter.id(),
        letter.authorProfileId(),
        letter.topicId(),
        letter.degree(),
        letter.message(),
        letter.color(),
        letter.shapeType(),
        likeCount,
        letter.createdAt(),
        letter.updatedAt());
  }
}
