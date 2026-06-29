package org.sopt.makers.api.controller.official.recruit;

import static org.sopt.makers.api.controller.official.recruit.RecruitPageSuccessCode.GET_RECRUIT_MAIN_PAGE;
import static org.sopt.makers.api.controller.official.recruit.RecruitPageSuccessCode.GET_RECRUIT_PART_DETAIL;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.official.recruit.dto.RecruitPageResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.official.recruit.RecruitPart;
import org.sopt.makers.domain.official.recruit.facade.RecruitPageFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/official/recruit")
public class RecruitPageController implements RecruitPageApi {

  private final RecruitPageFacade recruitPageFacade;

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getRecruitMainPage() {
    RecruitPageFacade.RecruitMainPage result = recruitPageFacade.getRecruitMainPage();
    return ResponseFactory.success(
        GET_RECRUIT_MAIN_PAGE, RecruitPageResponse.MainPage.from(result));
  }

  @Override
  @GetMapping("/part")
  public ResponseEntity<BaseResponse<?>> getPartDetail(@RequestParam String part) {
    RecruitPageFacade.RecruitPartDetail result =
        recruitPageFacade.getRecruitPartDetail(RecruitPart.from(part));
    return ResponseFactory.success(
        GET_RECRUIT_PART_DETAIL, RecruitPageResponse.PartDetail.from(result));
  }
}
