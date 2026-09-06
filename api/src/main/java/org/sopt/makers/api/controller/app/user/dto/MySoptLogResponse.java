package org.sopt.makers.api.controller.app.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.sopt.makers.domain.app.home.MySoptLog;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MySoptLogResponse(
    @JsonProperty("isAppjamMode") boolean isAppjamMode,
    @JsonProperty("isActive") boolean isActive,
    @JsonProperty("isAppjamParticipant") boolean isAppjamParticipant,
    @JsonProperty("isFortuneChecked") boolean isFortuneChecked,
    String todayFortuneText,
    Integer soptampCount,
    Integer viewCount,
    Integer myClapCount,
    Integer clapCount,
    int totalPokeCount,
    int newFriendsPokeCount,
    int bestFriendsPokeCount,
    int soulmatesPokeCount) {

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
