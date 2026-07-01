package org.sopt.makers.api.controller.admin.banner.dto;

import static lombok.AccessLevel.PRIVATE;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.admin.banner.exception.BannerException;
import org.sopt.makers.domain.admin.banner.exception.BannerFailure;
import org.sopt.makers.domain.admin.banner.port.BannerFileStoragePort;
import org.sopt.makers.domain.admin.banner.service.BannerService;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor(access = PRIVATE)
public final class BannerRequest {

  public record BannerCreateOrModify(
      @Schema(description = "배너 위치", requiredMode = Schema.RequiredMode.REQUIRED) String location,
      @Schema(description = "컨텐츠 타입", requiredMode = Schema.RequiredMode.REQUIRED)
          String contentType,
      @Schema(description = "게시자", requiredMode = Schema.RequiredMode.REQUIRED) String publisher,
      @Schema(description = "시작일 (yyyy-MM-dd)", type = "string", format = "date") String startDate,
      @Schema(description = "종료일 (yyyy-MM-dd)", type = "string", format = "date") String endDate,
      @Schema(description = "링크") String link,
      @Schema(description = "PC용 이미지", type = "string", format = "binary") MultipartFile imagePc,
      @Schema(description = "모바일용 이미지", type = "string", format = "binary")
          MultipartFile imageMobile) {

    public BannerService.BannerCreateOrModifyCommand toCommand() {
      return new BannerService.BannerCreateOrModifyCommand(
          location,
          contentType,
          publisher,
          startDate,
          endDate,
          link,
          toUploadFile(imagePc),
          toUploadFile(imageMobile));
    }
  }

  private static BannerFileStoragePort.UploadFile toUploadFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new BannerException(BannerFailure.FILE_UPLOAD_FAILED);
    }
    try {
      return new BannerFileStoragePort.UploadFile(
          file.getOriginalFilename(), file.getContentType(), file.getSize(), file.getInputStream());
    } catch (IOException e) {
      throw new BannerException(BannerFailure.FILE_UPLOAD_FAILED);
    }
  }
}
