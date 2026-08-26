package org.sopt.makers.domain.app.soptletter.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.sopt.makers.domain.app.soptletter.SoptLetterProfile;
import org.sopt.makers.domain.app.soptletter.port.SoptLetterProfileRepositoryPort;

public final class InMemorySoptLetterProfileRepositoryPort
    implements SoptLetterProfileRepositoryPort {

  private final List<SoptLetterProfile> store = new ArrayList<>();
  private long sequence = 1L;

  @Override
  public Optional<SoptLetterProfile> findById(Long profileId) {
    return store.stream().filter(p -> p.id().equals(profileId)).findFirst();
  }

  @Override
  public Optional<SoptLetterProfile> findByUserId(Long userId) {
    return store.stream().filter(p -> p.userId().equals(userId)).findFirst();
  }

  @Override
  public List<SoptLetterProfile> findAllByIds(Collection<Long> profileIds) {
    return store.stream().filter(p -> profileIds.contains(p.id())).toList();
  }

  @Override
  public boolean existsByUserId(Long userId) {
    return findByUserId(userId).isPresent();
  }

  @Override
  public Set<String> findExistingNicknames(Collection<String> nicknames) {
    return store.stream()
        .map(SoptLetterProfile::nickname)
        .filter(nicknames::contains)
        .collect(Collectors.toSet());
  }

  @Override
  public SoptLetterProfile save(SoptLetterProfile profile) {
    SoptLetterProfile saved =
        new SoptLetterProfile(
            sequence++, profile.userId(), profile.nickname(), profile.isOnboarded());
    store.add(saved);
    return saved;
  }

  @Override
  public void completeOnboarding(Long profileId) {
    findById(profileId)
        .ifPresent(
            profile ->
                store.set(
                    store.indexOf(profile),
                    new SoptLetterProfile(
                        profile.id(), profile.userId(), profile.nickname(), true)));
  }
}
