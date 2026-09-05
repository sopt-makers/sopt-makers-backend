package org.sopt.makers.api.controller.app.poke.dto;

import org.sopt.makers.domain.app.poke.SimplePokeProfileData;
import org.sopt.makers.domain.user.PokeUserProfile;

public record SimplePokeProfile(
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

  public static SimplePokeProfile of(SimplePokeProfileData data) {
    return new SimplePokeProfile(
        data.userId(),
        data.profileImage(),
        data.name(),
        data.message(),
        data.generation(),
        data.part(),
        data.pokeNum(),
        data.relationName(),
        data.mutualRelationMessage(),
        data.isFirstMeet(),
        data.isAlreadyPoke(),
        data.isAnonymous(),
        data.anonymousName());
  }

  public static SimplePokeProfile ofNonFriend(PokeUserProfile profile) {
    return new SimplePokeProfile(
        profile.userId(),
        profile.profileImage() == null ? "" : profile.profileImage(),
        profile.name(),
        "",
        profile.generation(),
        profile.part(),
        0,
        "",
        "",
        true,
        false,
        false,
        "");
  }
}
