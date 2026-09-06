package org.sopt.makers.api.controller.admin.soptamp;

import static org.sopt.makers.api.controller.admin.soptamp.AdminSoptampSuccessCode.SUCCESS_CLEAR_SOPTAMP_DATA;
import static org.sopt.makers.api.controller.admin.soptamp.AdminSoptampSuccessCode.SUCCESS_INIT_POINTS;
import static org.sopt.makers.api.controller.admin.soptamp.AdminSoptampSuccessCode.SUCCESS_INIT_RANK_CACHE;
import static org.sopt.makers.api.controller.admin.soptamp.AdminSoptampSuccessCode.SUCCESS_UPDATE_UPSERT_SCHEDULE;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.soptamp.facade.AdminSoptampFacade;
import org.sopt.makers.domain.app.soptamp.service.SoptampBatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin/soptamp")
public class AdminSoptampController implements AdminSoptampApi {

  private final AdminSoptampFacade adminSoptampFacade;
  private final SoptampBatchService soptampBatchService;
  private final AdminSoptampPasswordVerifier adminSoptampPasswordVerifier;

  @Override
  @DeleteMapping("/clear")
  public ResponseEntity<BaseResponse<?>> clearSoptampData(
      @RequestParam(name = "password") String password,
      @RequestParam(name = "stamp", defaultValue = "true") boolean stamp,
      @RequestParam(name = "soptampUser", defaultValue = "true") boolean soptampUser) {
    adminSoptampPasswordVerifier.verify(password);
    adminSoptampFacade.clearSoptampData(stamp, soptampUser);
    return ResponseFactory.success(SUCCESS_CLEAR_SOPTAMP_DATA);
  }

  @Override
  @DeleteMapping("/point")
  public ResponseEntity<BaseResponse<?>> initPoints(
      @RequestParam(name = "password") String password) {
    adminSoptampPasswordVerifier.verify(password);
    adminSoptampFacade.initPoints();
    return ResponseFactory.success(SUCCESS_INIT_POINTS);
  }

  @Override
  @DeleteMapping("/cache")
  public ResponseEntity<BaseResponse<?>> initRankCache(
      @RequestParam(name = "password") String password) {
    adminSoptampPasswordVerifier.verify(password);
    adminSoptampFacade.initRankCache();
    return ResponseFactory.success(SUCCESS_INIT_RANK_CACHE);
  }

  @Override
  @PatchMapping("/upsert/schedule")
  public ResponseEntity<BaseResponse<?>> updateUpsertSchedule(
      @RequestParam(name = "password") String password, @RequestParam(name = "cron") String cron) {
    adminSoptampPasswordVerifier.verify(password);
    soptampBatchService.setUpsertCron(cron);
    return ResponseFactory.success(SUCCESS_UPDATE_UPSERT_SCHEDULE);
  }
}
