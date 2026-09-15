package org.sopt.makers.domain.app.poke;

public record SimplePokeProfileData(
    Long userId,
    String profileImage,
    String name,
    String message,
    Long generation,
    String part,
    int pokeNum,
    String relationName,
    String mutualRelationMessage,
    boolean isFirstMeet,
    boolean isAlreadyPoke,
    boolean isAnonymous,
    String anonymousName) {

  public static SimplePokeProfileData of(
      PokedUserInfo pokedUserInfo,
      String message,
      boolean isFirstMeet,
      boolean isAlreadyPoke,
      boolean isAnonymous) {
    Relationship relation = pokedUserInfo.relation();
    return new SimplePokeProfileData(
        pokedUserInfo.userId(),
        pokedUserInfo.profileImage() == null ? "" : pokedUserInfo.profileImage(),
        pokedUserInfo.name(),
        message == null ? "" : message,
        pokedUserInfo.generation(),
        pokedUserInfo.part(),
        relation.pokeNum(),
        relation.relationName(),
        pokedUserInfo.getMutualRelationMessage(),
        isFirstMeet,
        isAlreadyPoke,
        isAnonymous,
        isAnonymous ? relation.anonymousName() : "");
  }
}
