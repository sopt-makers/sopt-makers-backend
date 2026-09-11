package org.sopt.makers.api.controller.app.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.makers.domain.app.home.MySoptLog;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MySoptLogResponse(
    @Schema(description = "앱잼 기간인지 여부", example = "false") @JsonProperty("isAppjamMode")
        boolean isAppjamMode,
    @Schema(description = "현재 기수 활동 중인지 여부", example = "true") @JsonProperty("isActive")
        boolean isActive,
    @Schema(description = "앱잼에 참여 중인지 여부", example = "false") @JsonProperty("isAppjamParticipant")
        boolean isAppjamParticipant,
    @Schema(description = "오늘 운세를 확인했는지 여부", example = "true") @JsonProperty("isFortuneChecked")
        boolean isFortuneChecked,
    @Schema(description = "오늘의 솝마디. 오늘 운세를 안 봤으면 안내 문구가 대신 들어간다", example = "오늘 내 운세는?")
        String todayFortuneText,
    @Schema(description = "작성한 솝탬프 개수. 값이 없으면 응답에서 빠진다", example = "5") Integer soptampCount,
    @Schema(description = "내 솝탬프가 조회된 횟수. 값이 없으면 응답에서 빠진다", example = "120") Integer viewCount,
    @Schema(description = "내 솝탬프가 받은 박수 횟수. 값이 없으면 응답에서 빠진다", example = "30") Integer myClapCount,
    @Schema(description = "내가 다른 사람에게 보낸 박수 횟수. 값이 없으면 응답에서 빠진다", example = "12") Integer clapCount,
    @Schema(description = "전체 콕 찌르기 횟수", example = "42") int totalPokeCount,
    @Schema(description = "친한친구 구간 상대와 주고받은 콕 찌르기 횟수", example = "10") int newFriendsPokeCount,
    @Schema(description = "단짝친구 구간 상대와 주고받은 콕 찌르기 횟수", example = "20") int bestFriendsPokeCount,
    @Schema(description = "천생연분 구간 상대와 주고받은 콕 찌르기 횟수", example = "12") int soulmatesPokeCount) {

  public static MySoptLogResponse of(MySoptLog mySoptLog) {
    return new MySoptLogResponse(
        mySoptLog.isAppjamMode(),
        mySoptLog.isActive(),
        mySoptLog.isAppjamParticipant(),
        mySoptLog.isFortuneChecked(),
        mySoptLog.todayFortuneText(),
        mySoptLog.soptampCount(),
        mySoptLog.viewCount(),
        mySoptLog.myClapCount(),
        mySoptLog.clapCount(),
        mySoptLog.totalPokeCount(),
        mySoptLog.newFriendsPokeCount(),
        mySoptLog.bestFriendsPokeCount(),
        mySoptLog.soulmatesPokeCount());
  }
}
