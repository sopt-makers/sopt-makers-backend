package org.sopt.makers.domain.app.home;

public record MySoptLog(
    boolean isAppjamMode,
    boolean isActive,
    boolean isAppjamParticipant,
    Integer soptampCount,
    Integer viewCount,
    Integer myClapCount,
    Integer clapCount,
    int totalPokeCount,
    int newFriendsPokeCount,
    int bestFriendsPokeCount,
    int soulmatesPokeCount) {

  public static MySoptLog ofActive(
      boolean isAppjamMode,
      boolean isAppjamParticipant,
      int soptampCount,
      int viewCount,
      int myClapCount,
      int clapCount,
      int totalPokeCount,
      int newFriendsPokeCount,
      int bestFriendsPokeCount,
      int soulmatesPokeCount) {
    return new MySoptLog(
        isAppjamMode,
        true,
        isAppjamParticipant,
        soptampCount,
        viewCount,
        myClapCount,
        clapCount,
        totalPokeCount,
        newFriendsPokeCount,
        bestFriendsPokeCount,
        soulmatesPokeCount);
  }

  public static MySoptLog ofInactiveAppjamParticipant(
      boolean isAppjamMode,
      int soptampCount,
      int viewCount,
      int myClapCount,
      int clapCount,
      int totalPokeCount,
      int newFriendsPokeCount,
      int bestFriendsPokeCount,
      int soulmatesPokeCount) {
    return new MySoptLog(
        isAppjamMode,
        false,
        true,
        soptampCount,
        viewCount,
        myClapCount,
        clapCount,
        totalPokeCount,
        newFriendsPokeCount,
        bestFriendsPokeCount,
        soulmatesPokeCount);
  }

  public static MySoptLog ofInactiveNonAppjam(
      boolean isAppjamMode,
      int totalPokeCount,
      int newFriendsPokeCount,
      int bestFriendsPokeCount,
      int soulmatesPokeCount) {
    return new MySoptLog(
        isAppjamMode,
        false,
        false,
        null,
        null,
        null,
        null,
        totalPokeCount,
        newFriendsPokeCount,
        bestFriendsPokeCount,
        soulmatesPokeCount);
  }
}
