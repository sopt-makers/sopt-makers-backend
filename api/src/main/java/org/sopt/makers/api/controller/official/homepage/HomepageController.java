package org.sopt.makers.api.controller.official.homepage;

import static org.sopt.makers.api.controller.official.homepage.HomepageSuccessCode.GET_HOMEPAGE_ABOUT;
import static org.sopt.makers.api.controller.official.homepage.HomepageSuccessCode.GET_HOMEPAGE_MAIN;
import static org.sopt.makers.api.controller.official.homepage.HomepageSuccessCode.GET_HOMEPAGE_RECRUIT;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.official.homepage.dto.HomepageAboutResponse;
import org.sopt.makers.api.controller.official.homepage.dto.HomepageMainResponse;
import org.sopt.makers.api.controller.official.homepage.dto.HomepageRecruitResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.official.homepage.facade.HomepageFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/official/homepage")
@RequiredArgsConstructor
public class HomepageController implements HomepageApi {

  private final HomepageFacade homepageFacade;

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getMainPage() {
    HomepageMainResponse response = HomepageMainResponse.from(homepageFacade.getMainPage());
    return ResponseFactory.success(GET_HOMEPAGE_MAIN, response);
  }

  @Override
  @GetMapping("/about")
  public ResponseEntity<BaseResponse<?>> getAboutPage() {
    HomepageAboutResponse response = HomepageAboutResponse.from(homepageFacade.getAboutPage());
    return ResponseFactory.success(GET_HOMEPAGE_ABOUT, response);
  }

  @Override
  @GetMapping("/recruit")
  public ResponseEntity<BaseResponse<?>> getRecruitPage() {
    HomepageRecruitResponse response =
        HomepageRecruitResponse.from(homepageFacade.getRecruitPage());
    return ResponseFactory.success(GET_HOMEPAGE_RECRUIT, response);
  }
}
