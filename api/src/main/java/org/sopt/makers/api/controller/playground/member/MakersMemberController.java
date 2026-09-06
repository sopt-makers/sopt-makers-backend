package org.sopt.makers.api.controller.playground.member;

import static org.sopt.makers.api.controller.playground.member.MakersMemberSuccessCode.GET_MAKERS_PROFILES;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.playground.member.dto.MakersMemberProfileResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.member.profile.service.UserProfileQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class MakersMemberController implements MakersMemberApi {

  private final UserProfileQueryService userProfileQueryService;

  @Override
  @GetMapping("/makers/profile")
  public ResponseEntity<BaseResponse<?>> getMakersProfiles() {
    List<MakersMemberProfileResponse> responses =
        userProfileQueryService.getMakersProfiles().stream().map(MakersMemberProfileResponse::from).toList();
    return ResponseFactory.success(GET_MAKERS_PROFILES, responses);
  }
}
