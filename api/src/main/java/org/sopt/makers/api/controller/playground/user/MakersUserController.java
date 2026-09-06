package org.sopt.makers.api.controller.playground.user;

import static org.sopt.makers.api.controller.playground.user.MakersUserSuccessCode.GET_MAKERS_PROFILES;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.playground.user.dto.MakersUserProfileResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.member.profile.service.UserProfileQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class MakersUserController implements MakersUserApi {

  private final UserProfileQueryService userProfileQueryService;

  @Override
  @GetMapping("/makers/profile")
  public ResponseEntity<BaseResponse<?>> getMakersProfiles() {
    List<MakersUserProfileResponse> responses =
        userProfileQueryService.getMakersProfiles().stream().map(MakersUserProfileResponse::from).toList();
    return ResponseFactory.success(GET_MAKERS_PROFILES, responses);
  }
}
