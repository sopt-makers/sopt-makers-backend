package org.sopt.makers.api.controller.app.firebase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FirebaseResponse(
    @JsonProperty("iOS_force_update_version") String iosForceUpdateVersion,
    @JsonProperty("iOS_app_version") String iosAppVersion,
    @JsonProperty("android_force_update_version") String androidForceUpdateVersion,
    @JsonProperty("android_app_version") String androidAppVersion,
    String notice,
    @JsonProperty("img_url") String imgUrl) {

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
