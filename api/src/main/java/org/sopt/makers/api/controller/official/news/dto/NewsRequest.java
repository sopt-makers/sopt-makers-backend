package org.sopt.makers.api.controller.official.news.dto;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.official.news.port.NewsFileStoragePort;
import org.sopt.makers.domain.official.news.service.NewsService;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor(access = PRIVATE)
public final class NewsRequest {

  public record CreateWithFile(
      @NotNull(message = "이미지는 필수 입력 값입니다.") MultipartFile image,
      @NotBlank(message = "제목은 필수 입력 값입니다.") String title,
      @NotBlank(message = "링크는 필수 입력 값입니다.") String link) {

    public NewsService.CreateNewsWithFileCommand toCommand() throws IOException {
      return new NewsService.CreateNewsWithFileCommand(toUploadFile(image), title, link);
    }
  }

  public record Create(
      @NotBlank(message = "이미지 URL은 필수 입력 값입니다.") String imageUrl,
      @NotBlank(message = "제목은 필수 입력 값입니다.") String title,
      @NotBlank(message = "링크는 필수 입력 값입니다.") String link) {

    public NewsService.CreateNewsCommand toCommand() {
      return new NewsService.CreateNewsCommand(imageUrl, title, link);
    }
  }

  public record EditWithFile(
      MultipartFile image,
      @NotBlank(message = "제목은 필수 입력 값입니다.") String title,
      @NotBlank(message = "링크는 필수 입력 값입니다.") String link) {

    public NewsService.EditNewsWithFileCommand toCommand() throws IOException {
      return new NewsService.EditNewsWithFileCommand(
          image == null || image.isEmpty() ? null : toUploadFile(image), title, link);
    }
  }

  public record Edit(
      @NotBlank(message = "이미지 URL은 필수 입력 값입니다.") String imageUrl,
      @NotBlank(message = "제목은 필수 입력 값입니다.") String title,
      @NotBlank(message = "링크는 필수 입력 값입니다.") String link) {

    public NewsService.EditNewsCommand toCommand() {
      return new NewsService.EditNewsCommand(imageUrl, title, link);
    }
  }

  public record Delete(@NotNull(message = "최신소식 ID는 필수 입력 값입니다.") Integer id) {}

  public record Get(@NotNull(message = "최신소식 ID는 필수 입력 값입니다.") Integer id) {}

  private static NewsFileStoragePort.UploadFile toUploadFile(MultipartFile file)
      throws IOException {
    return new NewsFileStoragePort.UploadFile(
        file.getOriginalFilename(), file.getContentType(), file.getSize(), file.getInputStream());
  }
}
