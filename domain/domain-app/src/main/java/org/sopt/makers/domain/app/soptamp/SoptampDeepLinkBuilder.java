package org.sopt.makers.domain.app.soptamp;

public final class SoptampDeepLinkBuilder {

  private static final String BASE =
      "soptamp/entire-part-ranking/part-ranking/missions/missionDetail";

  private SoptampDeepLinkBuilder() {}

  public static String buildStampDetailLink(
      long stampId,
      boolean isMine,
      String nickname,
      SoptampPart part,
      long missionId,
      int missionLevel,
      String missionTitle) {
    return String.format(
        "%s?id=%d&isMine=%s&nickname=%s&part=%s&missionId=%d&level=%d&missionTitle=%s",
        BASE,
        stampId,
        isMine,
        nickname,
        part.getShortName(),
        missionId,
        missionLevel,
        missionTitle);
  }
}
