package org.sopt.makers.domain.app.soptletter.port;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.sopt.makers.domain.app.soptletter.SoptLetterProfile;

public interface SoptLetterProfileRepositoryPort {

  Optional<SoptLetterProfile> findById(Long profileId);

  Optional<SoptLetterProfile> findByUserId(Long userId);

  List<SoptLetterProfile> findAllByIds(Collection<Long> profileIds);

  boolean existsByUserId(Long userId);

  Set<String> findExistingNicknames(Collection<String> nicknames);

  SoptLetterProfile save(SoptLetterProfile profile);

  void completeOnboarding(Long profileId);
}
