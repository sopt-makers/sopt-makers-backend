package org.sopt.makers.api.controller.admin.soptamp;

import static org.sopt.makers.api.controller.admin.soptamp.AdminSoptampSuccessCode.SUCCESS_SYNC_RANK_CACHE;
import static org.sopt.makers.api.controller.admin.soptamp.AdminSoptampSuccessCode.SUCCESS_UPSERT_SOPTAMP_USERS;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.admin.soptamp.dto.SoptampUpsertResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.soptamp.rank.service.RankService;
import org.sopt.makers.domain.app.soptamp.service.SoptampBatchService;
import org.sopt.makers.domain.app.soptamp.service.SoptampUpsertResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin/schedule")
public class AdminScheduleController implements AdminScheduleApi {

  private static final String ADMIN_PASSWORD_HEADER = "x-admin-password";

  private final RankService rankService;
  private final SoptampBatchService soptampBatchService;
  private final AdminSoptampPasswordVerifier adminSoptampPasswordVerifier;

  @Override
  @PostMapping("/soptamp/sync-rank-cache")
  public ResponseEntity<BaseResponse<?>> syncSoptampRankCache(
      @RequestHeader(ADMIN_PASSWORD_HEADER) String password) {
    adminSoptampPasswordVerifier.verify(password);
    rankService.reloadRankCache();
    return ResponseFactory.success(SUCCESS_SYNC_RANK_CACHE);
  }

  @Override
  @PostMapping("/soptamp/upsert")
  public ResponseEntity<BaseResponse<?>> upsertSoptampUsers(
      @RequestHeader(ADMIN_PASSWORD_HEADER) String password) {
    adminSoptampPasswordVerifier.verify(password);
    SoptampUpsertResult result = soptampBatchService.upsertAllSoptampUsers();
    return ResponseFactory.success(
        SUCCESS_UPSERT_SOPTAMP_USERS, SoptampUpsertResponse.from(result));
  }
}
