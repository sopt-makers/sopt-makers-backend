package org.sopt.makers.api.controller.admin.crew.advertisement;

import static org.sopt.makers.api.controller.admin.crew.advertisement.AdminCrewAdvertisementSuccessCode.UPDATE_MEETING_TOP_ADVERTISEMENT;
import static org.sopt.makers.api.controller.admin.crew.advertisement.AdminCrewAdvertisementSuccessCode.UPLOAD_MEETING_TOP_ADVERTISEMENT_IMAGE;
import static org.sopt.makers.domain.crew.advertisement.exception.AdvertisementFailure.INVALID_ADVERTISEMENT_IMAGE;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.admin.crew.advertisement.dto.AdvertisementImageUploadResponse;
import org.sopt.makers.api.controller.admin.crew.advertisement.dto.MeetingTopAdvertisementUpdateRequest;
import org.sopt.makers.api.controller.admin.crew.advertisement.dto.MeetingTopAdvertisementUpdateResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.crew.advertisement.exception.AdvertisementException;
import org.sopt.makers.domain.crew.advertisement.port.AdvertisementImageStoragePort;
import org.sopt.makers.domain.crew.advertisement.service.AdvertisementService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/crew/advertisements")
@RequiredArgsConstructor
public class AdminCrewAdvertisementController implements AdminCrewAdvertisementApi {

  private final AdvertisementService advertisementService;

  @Override
  @PatchMapping("/meeting-top/{advertisementId}")
  public ResponseEntity<BaseResponse<?>> updateMeetingTopAdvertisement(
      @PathVariable Integer advertisementId,
      @RequestBody MeetingTopAdvertisementUpdateRequest request) {
    return ResponseFactory.success(
        UPDATE_MEETING_TOP_ADVERTISEMENT,
        MeetingTopAdvertisementUpdateResponse.from(
            advertisementService.updateMeetingTopAdvertisement(
                advertisementId, request == null ? null : request.toValues())));
  }

  @Override
  @PostMapping(value = "/meeting-top/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<?>> uploadMeetingTopAdvertisementImage(
      @RequestPart("file") MultipartFile file) {
    try {
      String publicUrl =
          advertisementService.uploadMeetingTopImage(
              new AdvertisementImageStoragePort.UploadImage(
                  file.getOriginalFilename(),
                  file.getContentType(),
                  file.getSize(),
                  file.getInputStream()));
      return ResponseFactory.success(
          UPLOAD_MEETING_TOP_ADVERTISEMENT_IMAGE, new AdvertisementImageUploadResponse(publicUrl));
    } catch (IOException exception) {
      throw new AdvertisementException(INVALID_ADVERTISEMENT_IMAGE);
    }
  }
}
