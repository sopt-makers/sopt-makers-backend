package org.sopt.makers.api.controller.official.s3.dto;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.official.news.port.NewsFileStoragePort;

@NoArgsConstructor(access = PRIVATE)
public final class S3Request {

  public record PresignedUrl(
      @NotBlank(message = "파일명은 필수 입력 값입니다.") String fileName,
      @NotBlank(message = "Content-Type은 필수 입력 값입니다.")
          @Pattern(
              regexp = "^image/(jpeg|jpg|png|gif|webp)$",
              message = "허용된 이미지 타입은 jpeg, jpg, png, gif, webp입니다.")
          String contentType,
      String directory) {

    public NewsFileStoragePort.PresignedFileRequest toCommand() {
      return new NewsFileStoragePort.PresignedFileRequest(fileName, contentType, directory);
    }
  }
}
