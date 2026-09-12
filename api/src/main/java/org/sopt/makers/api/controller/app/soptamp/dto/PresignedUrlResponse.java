package org.sopt.makers.api.controller.app.soptamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.makers.domain.app.soptamp.stamp.port.StampFileStoragePort.PresignedFile;

public record PresignedUrlResponse(
    @Schema(
            description = "이미지를 PUT으로 올릴 주소. 유효 시간이 지나면 만료된다",
            example = "https://s3.ap-northeast-2.amazonaws.com/example/uuid?X-Amz-Algorithm=...")
        String preSignedURL,
    @Schema(description = "업로드가 끝난 뒤 쓸 이미지 주소", example = "https://s3.sopt.org/example/uuid")
        String imageURL) {

  public static PresignedUrlResponse of(PresignedFile file) {
    return new PresignedUrlResponse(file.presignedUrl(), file.fileUrl());
  }
}
