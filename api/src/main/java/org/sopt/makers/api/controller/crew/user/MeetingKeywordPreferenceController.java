package org.sopt.makers.api.controller.crew.user;

import static org.sopt.makers.api.controller.crew.user.MeetingKeywordPreferenceSuccessCode.GET_MEETING_KEYWORD_PREFERENCE;
import static org.sopt.makers.api.controller.crew.user.MeetingKeywordPreferenceSuccessCode.UPDATE_MEETING_KEYWORD_PREFERENCE;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.crew.user.dto.MeetingKeywordPreferenceResponse;
import org.sopt.makers.api.controller.crew.user.dto.UpdateMeetingKeywordPreferenceRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.crew.meeting.tag.service.MeetingKeywordPreferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/v2/interestedKeywords")
@RequiredArgsConstructor
public class MeetingKeywordPreferenceController implements MeetingKeywordPreferenceApi {

  private final MeetingKeywordPreferenceService preferenceService;

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> updateMeetingKeywordPreference(
      @RequestBody UpdateMeetingKeywordPreferenceRequest request, @CurrentUserId Long userId) {
    preferenceService.update(userId, request.toDomain());
    return ResponseFactory.success(UPDATE_MEETING_KEYWORD_PREFERENCE);
  }

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getMeetingKeywordPreference(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_MEETING_KEYWORD_PREFERENCE,
        MeetingKeywordPreferenceResponse.from(preferenceService.getByUserId(userId)));
  }
}
