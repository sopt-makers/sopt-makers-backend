package org.sopt.makers.api.controller.app.soptamp.dto;

import org.sopt.makers.domain.app.soptamp.stamp.port.StampFileStoragePort.PresignedFile;

public record PresignedUrlResponse(String preSignedURL, String imageURL) {

  public static PresignedUrlResponse of(PresignedFile file) {
    return new PresignedUrlResponse(file.presignedUrl(), file.fileUrl());
  }
}
