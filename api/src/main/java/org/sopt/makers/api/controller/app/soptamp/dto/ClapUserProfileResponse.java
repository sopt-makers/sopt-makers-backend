package org.sopt.makers.api.controller.app.soptamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.makers.domain.app.soptamp.clap.ClapUserProfile;

public record ClapUserProfileResponse(
    @Schema(description = "솝탬프 닉네임", example = "김앱짱") String nickname,
    @Schema(description = "프로필 이미지 주소. 프로필이 없으면 빈 문자열", example = "https://s3.sopt.org/profile.png")
        String profileImageUrl,
    @Schema(description = "프로필 한마디. 없으면 빈 문자열", example = "안녕하세요") String profileMessage,
    @Schema(description = "이 유저가 보낸 박수 수", example = "5") int clapCount) {

  public static ClapUserProfileResponse of(ClapUserProfile profile) {
    return new ClapUserProfileResponse(
        profile.nickname(),
        profile.profileImageUrl(),
        profile.profileMessage(),
        profile.clapCount());
  }
}
