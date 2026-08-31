package org.sopt.makers.api.controller.admin.crew.advertisement;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.admin.crew.advertisement.dto.MeetingTopAdvertisementUpdateRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "어드민 CREW 광고", description = "CREW 모임 상단 광고 관리 API")
public interface AdminCrewAdvertisementApi {

  @Operation(summary = "모임 상단 광고 수정")
  ResponseEntity<BaseResponse<?>> updateMeetingTopAdvertisement(
      Integer advertisementId, MeetingTopAdvertisementUpdateRequest request);

  @Operation(summary = "모임 상단 광고 이미지 업로드")
  ResponseEntity<BaseResponse<?>> uploadMeetingTopAdvertisementImage(MultipartFile file);
}
