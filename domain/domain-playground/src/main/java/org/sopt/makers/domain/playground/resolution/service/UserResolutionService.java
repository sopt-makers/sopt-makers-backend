package org.sopt.makers.domain.playground.resolution.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.resolution.ResolutionTag;
import org.sopt.makers.domain.playground.resolution.UserResolution;
import org.sopt.makers.domain.playground.resolution.exception.ResolutionException;
import org.sopt.makers.domain.playground.resolution.exception.ResolutionFailure;
import org.sopt.makers.domain.playground.resolution.port.ResolutionUserPort;
import org.sopt.makers.domain.playground.resolution.port.UserResolutionLuckyPickRepositoryPort;
import org.sopt.makers.domain.playground.resolution.port.UserResolutionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserResolutionService {

  // TODO: 새 기수 시작 전 값 변경 필수
  private static final int CURRENT_GENERATION = 38;

  private final UserResolutionRepositoryPort userResolutionRepositoryPort;
  private final UserResolutionLuckyPickRepositoryPort userResolutionLuckyPickRepositoryPort;
  private final ResolutionUserPort resolutionUserPort;

  @Transactional(readOnly = true)
  public ResolutionResult getResolution(Long userId) {
    validateUserExists(userId);
    boolean hasDrawnLuckyPick =
        userResolutionLuckyPickRepositoryPort.existsByUserIdAndHasDrawnTrue(userId);
    return userResolutionRepositoryPort
        .findByUserIdAndGeneration(userId, CURRENT_GENERATION)
        .map(r -> ResolutionResult.of(true, r.resolutionTags(), r.content(), hasDrawnLuckyPick))
        .orElseGet(() -> ResolutionResult.of(false, null, null, hasDrawnLuckyPick));
  }

  @Transactional(readOnly = true)
  public boolean isRegistered(Long userId) {
    validateUserExists(userId);
    return userResolutionRepositoryPort.existsByUserIdAndGeneration(userId, CURRENT_GENERATION);
  }

  @Transactional
  public void createResolution(Long userId, String content, List<ResolutionTag> tags) {
    validateUserExists(userId);
    validateMemberHasActivities(userId);
    validateGeneration(userId);
    validateExistingResolution(userId);

    userResolutionRepositoryPort.save(
        new UserResolution(null, userId, content, CURRENT_GENERATION, tags));
  }

  @Transactional
  public void deleteResolution(Long userId) {
    validateUserExists(userId);
    validateMemberHasActivities(userId);
    validateGeneration(userId);

    UserResolution resolution =
        userResolutionRepositoryPort
            .findByUserIdAndGeneration(userId, CURRENT_GENERATION)
            .orElseThrow(() -> new ResolutionException(ResolutionFailure.NOT_FOUND_RESOLUTION));

    userResolutionRepositoryPort.delete(resolution);
  }

  private void validateUserExists(Long userId) {
    if (!resolutionUserPort.existsById(userId)) {
      throw new ResolutionException(ResolutionFailure.NOT_FOUND_USER);
    }
  }

  private void validateMemberHasActivities(Long userId) {
    if (!resolutionUserPort.hasActivities(userId)) {
      throw new ResolutionException(ResolutionFailure.NO_ACTIVITIES);
    }
  }

  private void validateGeneration(Long userId) {
    if (resolutionUserPort.getLastGeneration(userId) != CURRENT_GENERATION) {
      throw new ResolutionException(ResolutionFailure.NOT_CURRENT_GENERATION);
    }
  }

  private void validateExistingResolution(Long userId) {
    if (userResolutionRepositoryPort.existsByUserIdAndGeneration(userId, CURRENT_GENERATION)) {
      throw new ResolutionException(ResolutionFailure.ALREADY_EXISTS_RESOLUTION);
    }
  }

  public record ResolutionResult(
      boolean hasWrittenTimeCapsule,
      List<ResolutionTag> tags,
      String content,
      boolean hasDrawnLuckyPick) {
    public static ResolutionResult of(
        boolean hasWrittenTimeCapsule,
        List<ResolutionTag> tags,
        String content,
        boolean hasDrawnLuckyPick) {
      return new ResolutionResult(hasWrittenTimeCapsule, tags, content, hasDrawnLuckyPick);
    }
  }
}
