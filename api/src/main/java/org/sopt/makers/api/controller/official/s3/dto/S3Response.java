package org.sopt.makers.api.controller.official.s3.dto;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import org.sopt.makers.domain.official.news.port.NewsFileStoragePort;

@NoArgsConstructor(access = PRIVATE)
public final class S3Response {

  public record PresignedUrl(String presignedUrl, String fileUrl, long expiresIn, String fileKey) {

    public static PresignedUrl of(NewsFileStoragePort.PresignedFile file) {
      return new PresignedUrl(
          file.presignedUrl(), file.fileUrl(), file.expiresIn(), file.fileKey());
    }
  }
}
