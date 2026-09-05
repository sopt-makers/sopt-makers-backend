package org.sopt.makers.api.controller.crew.post.dto;

import java.util.List;
import org.sopt.makers.domain.playground.post.service.PostService;

public record MumuHomeResponse(
    Boolean isEmptyAppliedMeeting,
    Boolean hasWrittenTodayMumuPost,
    Boolean hasMumuPostHomeFeed,
    String mumuText,
    List<MumuPostResponse> mumuPostHomeDtos) {

  public static MumuHomeResponse from(PostService.MumuHome home) {
    return new MumuHomeResponse(
        home.emptyAppliedMeeting(),
        home.writtenToday(),
        home.hasHomeFeed(),
        home.mumuText(),
        home.posts().stream().map(MumuPostResponse::from).toList());
  }
}
