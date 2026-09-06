package org.sopt.makers.domain.app.soptamp.support;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.sopt.makers.domain.app.soptamp.clap.Clap;
import org.sopt.makers.domain.app.soptamp.clap.port.ClapMilestonePort;
import org.sopt.makers.domain.app.soptamp.clap.port.ClapRepositoryPort;
import org.springframework.data.domain.Pageable;

public final class InMemoryClapStore implements ClapRepositoryPort, ClapMilestonePort {

  private final List<Clap> claps = new ArrayList<>();
  private final Set<String> milestoneHits = new HashSet<>();
  private final Deque<RuntimeException> saveFailures = new ArrayDeque<>();
  private long sequence = 0;

  public void failNextSaves(RuntimeException... failures) {
    saveFailures.addAll(List.of(failures));
  }

  @Override
  public Optional<Clap> findByUserIdAndStampId(Long userId, Long stampId) {
    return claps.stream()
        .filter(it -> it.userId().equals(userId) && it.stampId().equals(stampId))
        .findFirst();
  }

  @Override
  public List<Clap> findAllByStampIdOrderByClapCountDesc(Long stampId, Pageable pageable) {
    return claps.stream()
        .filter(it -> it.stampId().equals(stampId))
        .sorted(Comparator.comparingInt(Clap::clapCount).reversed())
        .skip(pageable.getOffset())
        .limit(pageable.getPageSize())
        .toList();
  }

  @Override
  public long countByStampId(Long stampId) {
    return claps.stream().filter(it -> it.stampId().equals(stampId)).count();
  }

  @Override
  public List<Clap> findAllByUserId(Long userId) {
    return claps.stream().filter(it -> it.userId().equals(userId)).toList();
  }

  @Override
  public Clap save(Clap clap) {
    if (!saveFailures.isEmpty()) {
      throw saveFailures.poll();
    }
    Clap saved =
        clap.id() == null
            ? new Clap(++sequence, clap.stampId(), clap.userId(), clap.clapCount(), 0L)
            : clap;
    claps.removeIf(
        it -> it.userId().equals(saved.userId()) && it.stampId().equals(saved.stampId()));
    claps.add(saved);
    return saved;
  }

  @Override
  public void deleteAll() {
    claps.clear();
    milestoneHits.clear();
  }

  @Override
  public boolean tryMarkFirstHit(Long stampId, int milestone) {
    return milestoneHits.add(stampId + ":" + milestone);
  }
}
