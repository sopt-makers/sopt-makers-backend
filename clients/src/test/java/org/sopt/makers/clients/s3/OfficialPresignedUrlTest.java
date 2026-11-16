package org.sopt.makers.clients.s3;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.sopt.makers.clients.s3.exception.S3Exception;
import org.sopt.makers.domain.official.news.port.NewsFileStoragePort;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

class OfficialPresignedUrlTest {
  @Test
  @DisplayName("서명 URL은 10분간 유효하며 파일 접근 URL과 객체 키를 함께 반환한다")
  void signsUploadUrl() {
    var result = generate("news/", "image/png");
    assertThat(result.expiresIn()).isEqualTo(600);
    assertThat(result.fileKey()).matches("develop/news/[0-9a-f-]{36}_news.png");
    assertThat(result.fileUrl())
        .isEqualTo("https://s3.ap-northeast-2.amazonaws.com/test-bucket/" + result.fileKey());
    assertThat(result.presignedUrl())
        .startsWith("https://test-bucket.s3.ap-northeast-2.amazonaws.com/")
        .contains("X-Amz-Expires=600");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   "})
  @DisplayName("디렉터리를 생략하거나 공백이면 기본 경로에 파일 키를 생성한다")
  void usesBaseDirectory(String directory) {
    assertThat(generate(directory, "image/png").fileKey())
        .matches("develop/[0-9a-f-]{36}_news.png");
  }

  @ParameterizedTest
  @ValueSource(strings = {"news", "news/", "/news/", " news "})
  @DisplayName("디렉터리의 앞뒤 공백과 구분자를 정리한다")
  void normalizesDirectory(String directory) {
    assertThat(generate(directory, "image/png").fileKey())
        .matches("develop/news/[0-9a-f-]{36}_news.png");
  }

  @Test
  @DisplayName("허용되지 않은 콘텐츠 타입은 서명 URL을 발급하지 않는다")
  void rejectsUnsupportedContentType() {
    assertThatThrownBy(() -> generate("news/", "application/pdf")).isInstanceOf(S3Exception.class);
  }

  private NewsFileStoragePort.PresignedFile generate(String directory, String type) {
    var credentials =
        StaticCredentialsProvider.create(
            AwsBasicCredentials.create("test-access-key", "test-secret-key"));
    try (var presigner =
            S3Presigner.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(credentials)
                .build();
        var client =
            S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(credentials)
                .build()) {
      var adapter =
          new S3FileStorageAdapter(
              presigner, client, new S3Property("test-bucket", "ap-northeast-2", "develop/"));
      return adapter.generatePresignedUrl(
          new NewsFileStoragePort.PresignedFileRequest("news.png", type, directory));
    }
  }
}
