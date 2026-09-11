package org.sopt.makers.api.controller.app.poke.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.makers.domain.app.poke.SimplePokeProfileData;
import org.sopt.makers.domain.user.PokeUserProfile;

public record SimplePokeProfile(
    @Schema(description = "앱 유저 아이디", example = "1") Long userId,
    @Schema(description = "프로필 이미지 주소. 없으면 빈 문자열", example = "https://s3.sopt.org/profile.png")
        String profileImage,
    @Schema(description = "유저 이름", example = "김앱짱") String name,
    @Schema(description = "찌르면서 보낸 메시지. 친구 추천 목록에서는 빈 문자열", example = "안녕") String message,
    @Schema(description = "활동 기수", example = "35") Long generation,
    @Schema(description = "활동 파트", example = "서버") String part,
    @Schema(description = "현재까지 찌른 횟수", example = "3") int pokeNum,
    @Schema(description = "관계 이름. 아직 친구가 아니면 빈 문자열", example = "천생연분") String relationName,
    @Schema(description = "함께 아는 친구관계 문구", example = "제갈송현 외 1명과 친구") String mutualRelationMessage,
    @Schema(description = "누적 찌른 횟수가 2회 미만인지 여부", example = "false") boolean isFirstMeet,
    @Schema(description = "내가 찌른 뒤 아직 답장받지 못한 이력이 있는지 여부", example = "true") boolean isAlreadyPoke,
    @Schema(description = "익명으로 찔렀는지 여부", example = "true") boolean isAnonymous,
    @Schema(description = "익명 이름. 익명이 아니면 빈 문자열", example = "익명의 그윽한 떡볶이") String anonymousName) {

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
