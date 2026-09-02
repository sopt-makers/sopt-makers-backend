package org.sopt.makers.domain.app.soptamp;

public record SoptampUser(
    Long id,
    Long userId,
    String profileMessage,
    Long totalPoints,
    String nickname,
    Long generation,
    SoptampPart part) {

  public SoptampUser {
    profileMessage = profileMessage == null ? "" : profileMessage;
    totalPoints = totalPoints == null ? 0L : totalPoints;
  }

  public static SoptampUser create(
      Long userId, String nickname, Long generation, SoptampPart part) {
    return new SoptampUser(null, userId, "", 0L, nickname, generation, part);
  }

  public SoptampUser refreshForGeneration(String nickname, Long generation, SoptampPart part) {
    return new SoptampUser(id, userId, profileMessage, 0L, nickname, generation, part);
  }

  public SoptampUser withProfileMessage(String newProfileMessage) {
    return new SoptampUser(id, userId, newProfileMessage, totalPoints, nickname, generation, part);
  }

  public SoptampUser addPointsByLevel(int level) {
    return new SoptampUser(
        id, userId, profileMessage, totalPoints + level, nickname, generation, part);
  }

  public SoptampUser subtractPointsByLevel(int level) {
    return new SoptampUser(
        id, userId, profileMessage, totalPoints - level, nickname, generation, part);
  }

  public SoptampUser initTotalPoints() {
    return new SoptampUser(id, userId, profileMessage, 0L, nickname, generation, part);
  }
}
