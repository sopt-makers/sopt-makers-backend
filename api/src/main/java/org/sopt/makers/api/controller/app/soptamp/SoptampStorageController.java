package org.sopt.makers.api.controller.app.soptamp;

import static org.sopt.makers.api.controller.app.soptamp.SoptampStorageSuccessCode.GET_MISSION_PRESIGNED_URL;
import static org.sopt.makers.api.controller.app.soptamp.SoptampStorageSuccessCode.GET_STAMP_PRESIGNED_URL;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.app.soptamp.dto.PresignedUrlResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.soptamp.facade.SoptampFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/s3")
@RequiredArgsConstructor
public class SoptampStorageController implements SoptampStorageApi {

  private final SoptampFacade soptampFacade;

  @Override
  @GetMapping("/stamp")
  public ResponseEntity<BaseResponse<?>> getStampPreSignedUrl() {
    return ResponseFactory.success(
        GET_STAMP_PRESIGNED_URL,
        PresignedUrlResponse.of(soptampFacade.generateStampImagePresignedUrl()));
  }

  @Override
  @GetMapping("/mission")
  public ResponseEntity<BaseResponse<?>> getMissionPreSignedUrl() {
    return ResponseFactory.success(
        GET_MISSION_PRESIGNED_URL,
        PresignedUrlResponse.of(soptampFacade.generateMissionImagePresignedUrl()));
  }
}
