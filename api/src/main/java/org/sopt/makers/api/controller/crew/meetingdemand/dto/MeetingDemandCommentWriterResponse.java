package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import org.sopt.makers.domain.crew.meeting.demand.comment.MeetingDemandCommentProfile;

public record MeetingDemandCommentWriterResponse(
    String anonymousNickname, String anonymousImageUrl) {

  public static MeetingDemandCommentWriterResponse from(MeetingDemandCommentProfile profile) {
    return profile == null
        ? null
        : new MeetingDemandCommentWriterResponse(
            profile.anonymousNickname(), profile.anonymousImageUrl());
  }
}
