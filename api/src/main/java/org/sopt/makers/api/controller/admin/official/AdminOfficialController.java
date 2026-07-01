package org.sopt.makers.api.controller.admin.official;

import static org.sopt.makers.api.controller.admin.official.AdminOfficialSuccessCode.SUCCESS_ADD_ABOUT;
import static org.sopt.makers.api.controller.admin.official.AdminOfficialSuccessCode.SUCCESS_ADD_COMMON;
import static org.sopt.makers.api.controller.admin.official.AdminOfficialSuccessCode.SUCCESS_ADD_HOME;
import static org.sopt.makers.api.controller.admin.official.AdminOfficialSuccessCode.SUCCESS_ADD_RECRUIT;
import static org.sopt.makers.api.controller.admin.official.AdminOfficialSuccessCode.SUCCESS_CONFIRM_ABOUT;
import static org.sopt.makers.api.controller.admin.official.AdminOfficialSuccessCode.SUCCESS_CONFIRM_COMMON;
import static org.sopt.makers.api.controller.admin.official.AdminOfficialSuccessCode.SUCCESS_CONFIRM_HOME;
import static org.sopt.makers.api.controller.admin.official.AdminOfficialSuccessCode.SUCCESS_CONFIRM_RECRUIT;
import static org.sopt.makers.api.controller.admin.official.AdminOfficialSuccessCode.SUCCESS_GET_ADMIN_MAIN;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.admin.official.dto.AdminOfficialRequest;
import org.sopt.makers.api.controller.admin.official.dto.AdminOfficialResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.official.admin.facade.AdminOfficialFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/official")
public class AdminOfficialController implements AdminOfficialApi {

  private final AdminOfficialFacade adminOfficialFacade;

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getAdminMain(
      @RequestParam("generation") Integer generation) {
    return ResponseFactory.success(
        SUCCESS_GET_ADMIN_MAIN,
        AdminOfficialResponse.AdminMainResponse.from(adminOfficialFacade.getMain(generation)));
  }

  @Override
  @PostMapping("/common")
  public ResponseEntity<BaseResponse<?>> addCommonData(
      @RequestBody AdminOfficialRequest.AddCommonRequest request) {
    return ResponseFactory.success(
        SUCCESS_ADD_COMMON,
        AdminOfficialResponse.AddCommonResponse.from(
            adminOfficialFacade.addCommonData(request.toCommand())));
  }

  @Override
  @PostMapping("/common/confirm")
  public ResponseEntity<BaseResponse<?>> confirmCommonData(
      @RequestBody AdminOfficialRequest.ConfirmRequest request) {
    return ResponseFactory.success(
        SUCCESS_CONFIRM_COMMON,
        AdminOfficialResponse.ConfirmResponse.from(
            adminOfficialFacade.addCommonDataConfirm(request.generation())));
  }

  @Override
  @PostMapping("/home")
  public ResponseEntity<BaseResponse<?>> addHomeData(
      @RequestBody AdminOfficialRequest.AddHomeRequest request) {
    return ResponseFactory.success(
        SUCCESS_ADD_HOME,
        AdminOfficialResponse.AddHomeResponse.from(
            adminOfficialFacade.addHomeData(request.toCommand())));
  }

  @Override
  @PostMapping("/home/confirm")
  public ResponseEntity<BaseResponse<?>> confirmHomeData(
      @RequestBody AdminOfficialRequest.ConfirmRequest request) {
    return ResponseFactory.success(
        SUCCESS_CONFIRM_HOME,
        AdminOfficialResponse.ConfirmResponse.from(
            adminOfficialFacade.addHomeDataConfirm(request.generation())));
  }

  @Override
  @PostMapping("/about")
  public ResponseEntity<BaseResponse<?>> addAboutData(
      @RequestBody AdminOfficialRequest.AddAboutRequest request) {
    return ResponseFactory.success(
        SUCCESS_ADD_ABOUT,
        AdminOfficialResponse.AddAboutResponse.from(
            adminOfficialFacade.addAboutData(request.toCommand())));
  }

  @Override
  @PostMapping("/about/confirm")
  public ResponseEntity<BaseResponse<?>> confirmAboutData(
      @RequestBody AdminOfficialRequest.ConfirmRequest request) {
    return ResponseFactory.success(
        SUCCESS_CONFIRM_ABOUT,
        AdminOfficialResponse.ConfirmResponse.from(
            adminOfficialFacade.addAboutDataConfirm(request.generation())));
  }

  @Override
  @PostMapping("/recruit")
  public ResponseEntity<BaseResponse<?>> addRecruitData(
      @RequestBody AdminOfficialRequest.AddRecruitRequest request) {
    return ResponseFactory.success(
        SUCCESS_ADD_RECRUIT,
        AdminOfficialResponse.AddRecruitResponse.from(
            adminOfficialFacade.addRecruitData(request.toCommand())));
  }

  @Override
  @PostMapping("/recruit/confirm")
  public ResponseEntity<BaseResponse<?>> confirmRecruitData(
      @RequestBody AdminOfficialRequest.ConfirmRequest request) {
    return ResponseFactory.success(
        SUCCESS_CONFIRM_RECRUIT,
        AdminOfficialResponse.ConfirmResponse.from(
            adminOfficialFacade.addRecruitDataConfirm(request.generation())));
  }
}
