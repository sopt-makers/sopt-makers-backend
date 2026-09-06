package org.sopt.makers.domain.app.home;

public record MySoptLog(
    boolean isAppjamMode,
    boolean isActive,
    boolean isAppjamParticipant,
    boolean isFortuneChecked,
    String todayFortuneText,
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
      boolean isFortuneChecked,
      String todayFortuneText,
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
        isFortuneChecked,
        todayFortuneText,
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
      boolean isFortuneChecked,
      String todayFortuneText,
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
        isFortuneChecked,
        todayFortuneText,
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
      boolean isFortuneChecked,
      String todayFortuneText,
      int totalPokeCount,
      int newFriendsPokeCount,
      int bestFriendsPokeCount,
      int soulmatesPokeCount) {
    return new MySoptLog(
        isAppjamMode,
        false,
        false,
        isFortuneChecked,
        todayFortuneText,
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
