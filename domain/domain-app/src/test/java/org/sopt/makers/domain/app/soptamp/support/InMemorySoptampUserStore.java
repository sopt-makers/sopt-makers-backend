package org.sopt.makers.domain.app.soptamp.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.port.SoptampPointUpdaterPort;
import org.sopt.makers.domain.app.soptamp.port.SoptampUserCommandPort;
import org.sopt.makers.domain.app.soptamp.port.SoptampUserQueryPort;

public final class InMemorySoptampUserStore
    implements SoptampUserQueryPort, SoptampUserCommandPort, SoptampPointUpdaterPort {

  private final List<SoptampUser> store = new ArrayList<>();
  private final Long currentGeneration;
  private long sequence = 1L;

  public InMemorySoptampUserStore(long currentGeneration) {
    this.currentGeneration = currentGeneration;
  }

  @Override
  public SoptampUser save(SoptampUser user) {
    SoptampUser saved = user.id() == null ? withId(user, sequence++) : user;
    store.removeIf(it -> it.userId().equals(saved.userId()));
    store.add(saved);
    return saved;
  }

  private static SoptampUser withId(SoptampUser user, long id) {
    return new SoptampUser(
        id,
        user.userId(),
        user.profileMessage(),
        user.totalPoints(),
        user.nickname(),
        user.generation(),
        user.part());
  }

  @Override
  public Optional<SoptampUser> findByUserId(Long userId) {
    return store.stream().filter(it -> it.userId().equals(userId)).findFirst();
  }

  @Override
  public Optional<SoptampUser> findByNickname(String nickname) {
    return store.stream().filter(it -> nickname.equals(it.nickname())).findFirst();
  }

  @Override
  public List<SoptampUser> findAllOfCurrentGeneration() {
    return store.stream().filter(this::isCurrentGeneration).toList();
  }

  @Override
  public List<SoptampUser> findAllByPartAndCurrentGeneration(Part part) {
    String prefix = SoptampPart.of(part).getShortName();
    return store.stream()
        .filter(it -> it.nickname() != null && it.nickname().startsWith(prefix))
        .filter(this::isCurrentGeneration)
        .toList();
  }

  private boolean isCurrentGeneration(SoptampUser user) {
    return currentGeneration.equals(user.generation());
  }

  @Override
  public List<SoptampUser> findAllByUserIds(Collection<Long> userIds) {
    return store.stream().filter(it -> userIds.contains(it.userId())).toList();
  }

  @Override
  public List<SoptampUser> findAllByGeneration(Long generation) {
    return store.stream().filter(it -> generation.equals(it.generation())).toList();
  }

  @Override
  public Map<Long, SoptampUser> findByUserIdsAsMap(Collection<Long> userIds) {
    Map<Long, SoptampUser> found = new LinkedHashMap<>();
    store.stream()
        .filter(it -> userIds.contains(it.userId()))
        .forEach(it -> found.put(it.userId(), it));
    return found;
  }

  @Override
  public List<Long> findAllUserIds() {
    return store.stream().map(SoptampUser::userId).toList();
  }

  @Override
  public boolean existsByNickname(String nickname) {
    return findByNickname(nickname).isPresent();
  }

  @Override
  public boolean existsByNicknameAndUserIdNot(String nickname, Long userId) {
    return store.stream()
        .anyMatch(it -> nickname.equals(it.nickname()) && !it.userId().equals(userId));
  }

  @Override
  public SoptampUser updateProfileMessage(Long userId, String profileMessage) {
    SoptampUser user = get(userId);
    SoptampUser updated =
        new SoptampUser(
            user.id(),
            user.userId(),
            profileMessage,
            user.totalPoints(),
            user.nickname(),
            user.generation(),
            user.part());
    save(updated);
    return updated;
  }

  @Override
  public void create(Long userId, String nickname, Long generation, SoptampPart part) {
    save(SoptampUser.create(userId, nickname, generation, part));
  }

  @Override
  public void updateChangedGenerationInfo(
      Long userId, Long generation, SoptampPart part, String nickname) {
    SoptampUser user = get(userId);
    save(new SoptampUser(user.id(), userId, user.profileMessage(), 0L, nickname, generation, part));
  }

  @Override
  public void deleteAll() {
    store.clear();
  }

  @Override
  public void addPointByLevel(Long userId, int level) {
    updatePoints(userId, get(userId).totalPoints() + level);
  }

  @Override
  public void subtractPointByLevel(Long userId, int level) {
    updatePoints(userId, get(userId).totalPoints() - level);
  }

  @Override
  public void initPoint(Long userId) {
    updatePoints(userId, 0L);
  }

  @Override
  public void initAllPoints() {
    List.copyOf(store).forEach(it -> updatePoints(it.userId(), 0L));
  }

  private void updatePoints(Long userId, long totalPoints) {
    SoptampUser user = get(userId);
    save(
        new SoptampUser(
            user.id(),
            user.userId(),
            user.profileMessage(),
            totalPoints,
            user.nickname(),
            user.generation(),
            user.part()));
  }

  private SoptampUser get(Long userId) {
    return findByUserId(userId)
        .orElseThrow(() -> new SoptampException(SoptampFailure.NOT_FOUND_SOPTAMP_USER));
  }
}
