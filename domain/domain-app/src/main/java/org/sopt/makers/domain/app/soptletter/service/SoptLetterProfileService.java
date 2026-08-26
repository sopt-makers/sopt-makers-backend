package org.sopt.makers.domain.app.soptletter.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.app.soptletter.SoptLetterProfile;
import org.sopt.makers.domain.app.soptletter.exception.SoptLetterException;
import org.sopt.makers.domain.app.soptletter.exception.SoptLetterFailure;
import org.sopt.makers.domain.app.soptletter.port.AnonymousNicknameGeneratorPort;
import org.sopt.makers.domain.app.soptletter.port.SoptLetterProfileRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoptLetterProfileService {

  private static final int NICKNAME_RETRY_COUNT = 3;
  private static final int NICKNAME_CANDIDATE_SIZE = 3;

  private final SoptLetterProfileRepositoryPort soptLetterProfileRepositoryPort;
  private final AnonymousNicknameGeneratorPort anonymousNicknameGeneratorPort;

  public boolean isOnboarded(Long userId) {
    return soptLetterProfileRepositoryPort.existsByUserId(userId);
  }

  public SoptLetterProfile getByUserId(Long userId) {
    return soptLetterProfileRepositoryPort
        .findByUserId(userId)
        .orElseThrow(
            () -> new SoptLetterException(SoptLetterFailure.NOT_FOUND_SOPT_LETTER_PROFILE));
  }

  @Transactional
  public SoptLetterProfile getOrCreate(Long userId) {
    return soptLetterProfileRepositoryPort
        .findByUserId(userId)
        .orElseGet(
            () ->
                soptLetterProfileRepositoryPort.save(
                    SoptLetterProfile.create(userId, generateUniqueNickname())));
  }

  @Transactional
  public SoptLetterProfile completeOnboarding(Long userId) {
    SoptLetterProfile profile = getByUserId(userId);
    soptLetterProfileRepositoryPort.completeOnboarding(profile.id());
    return new SoptLetterProfile(profile.id(), profile.userId(), profile.nickname(), true);
  }

  private String generateUniqueNickname() {
    for (int attempt = 0; attempt < NICKNAME_RETRY_COUNT; attempt++) {
      List<String> candidates = anonymousNicknameGeneratorPort.generate(NICKNAME_CANDIDATE_SIZE);
      Set<String> taken = soptLetterProfileRepositoryPort.findExistingNicknames(candidates);
      Optional<String> available =
          candidates.stream().filter(candidate -> !taken.contains(candidate)).findFirst();
      if (available.isPresent()) {
        return available.get();
      }
    }
    log.error("솝레터 익명 닉네임을 {}번 시도했지만 사용 가능한 값을 찾지 못했습니다.", NICKNAME_RETRY_COUNT);
    throw new SoptLetterException(SoptLetterFailure.NICKNAME_IS_FULL);
  }
}
