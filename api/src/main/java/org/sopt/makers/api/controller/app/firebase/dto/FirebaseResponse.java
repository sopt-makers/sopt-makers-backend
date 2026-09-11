package org.sopt.makers.api.controller.app.firebase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record FirebaseResponse(
    @Schema(description = "이 버전 미만이면 강제 업데이트를 띄우는 iOS 최소 버전", example = "3.0.3")
        @JsonProperty("iOS_force_update_version")
        String iosForceUpdateVersion,
    @Schema(description = "스토어에 올라간 iOS 최신 버전", example = "2.2.0") @JsonProperty("iOS_app_version")
        String iosAppVersion,
    @Schema(description = "이 버전 미만이면 강제 업데이트를 띄우는 안드로이드 최소 버전", example = "1.0.0")
        @JsonProperty("android_force_update_version")
        String androidForceUpdateVersion,
    @Schema(description = "스토어에 올라간 안드로이드 최신 버전", example = "2.0.0")
        @JsonProperty("android_app_version")
        String androidAppVersion,
    @Schema(
            description = "업데이트 안내 문구",
            example = "안녕하세요, Makers 입니다. SOPT APP이 더 편리한 서비스 경험을 위해 개선 되었어요")
        String notice,
    @Schema(description = "안내 이미지 주소. 현재는 항상 null", example = "null") @JsonProperty("img_url")
        String imgUrl) {

  private static final String IOS_FORCE_UPDATE_VERSION = "3.0.3";
  private static final String IOS_APP_VERSION = "2.2.0";
  private static final String ANDROID_FORCE_UPDATE_VERSION = "1.0.0";
  private static final String ANDROID_APP_VERSION = "2.0.0";
  private static final String NOTICE =
      "안녕하세요, Makers 입니다. SOPT APP이 더 편리한 서비스 경험을 위해 개선 되었어요 ‘◡’\n"
          + "지금 바로 업데이트를 통해 더 편리하고, 안정적인 SOPT APP을 경험해보세요!\n";

  public static FirebaseResponse current() {
    return new FirebaseResponse(
        IOS_FORCE_UPDATE_VERSION,
        IOS_APP_VERSION,
        ANDROID_FORCE_UPDATE_VERSION,
        ANDROID_APP_VERSION,
        NOTICE,
        null);
  }
}
