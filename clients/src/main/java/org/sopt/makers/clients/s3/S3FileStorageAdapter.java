package org.sopt.makers.clients.s3;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.clients.s3.exception.S3Exception;
import org.sopt.makers.clients.s3.exception.S3Failure;
import org.sopt.makers.domain.official.homepage.news.port.NewsFileStoragePort;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileStorageAdapter implements NewsFileStoragePort {

  private static final long PRESIGNED_URL_EXPIRATION_MINUTES = 10;
  private static final Set<String> ALLOWED_CONTENT_TYPES =
      Set.of("image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp");
  private static final Map<String, String> CONTENT_TYPES_BY_EXTENSION =
      Map.of(
          "jpg", "image/jpeg",
          "jpeg", "image/jpeg",
          "png", "image/png",
          "gif", "image/gif",
          "webp", "image/webp",
          "pdf", "application/pdf",
          "txt", "text/plain",
          "html", "text/html",
          "json", "application/json");

  private final S3Presigner s3Presigner;
  private final S3Client s3Client;
  private final S3Property property;

  @Override
  public String upload(UploadFile file, String directory) {
    String fileName = createFileName(file.originalFilename());
    String fileKey = buildFileKey(directory, fileName);

    try {
      PutObjectRequest request =
          PutObjectRequest.builder()
              .bucket(property.bucket())
              .key(fileKey)
              .contentType(file.contentType())
              .build();
      s3Client.putObject(request, RequestBody.fromInputStream(file.inputStream(), file.size()));
      return getFileUrl(fileKey);
    } catch (Exception e) {
      log.error("S3 파일 업로드 실패 - fileKey={}", fileKey, e);
      throw new S3Exception(S3Failure.FILE_UPLOAD_FAILED);
    }
  }

  @Override
  public void delete(String fileUrl) {
    try {
      DeleteObjectRequest request =
          DeleteObjectRequest.builder().bucket(property.bucket()).key(extractKey(fileUrl)).build();
      s3Client.deleteObject(request);
    } catch (Exception e) {
      log.error("S3 파일 삭제 실패 - fileUrl={}", fileUrl, e);
      throw new S3Exception(S3Failure.FILE_DELETE_FAILED);
    }
  }

  @Override
  public PresignedFile generatePresignedUrl(PresignedFileRequest request) {
    validateContentType(request.contentType());

    String fileName = createFileName(request.fileName());
    String fileKey = buildFileKey(request.directory(), fileName);
    PutObjectRequest objectRequest =
        PutObjectRequest.builder()
            .bucket(property.bucket())
            .key(fileKey)
            .contentType(request.contentType())
            .build();
    PutObjectPresignRequest presignRequest =
        PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(PRESIGNED_URL_EXPIRATION_MINUTES))
            .putObjectRequest(objectRequest)
            .build();

    PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
    return new PresignedFile(
        presignedRequest.url().toString(),
        getFileUrl(fileKey),
        PRESIGNED_URL_EXPIRATION_MINUTES * 60,
        fileKey);
  }

  public String generatePresignedUrl(String fileName, String directory) {
    if (isCompleteS3Url(fileName)) {
      return fileName;
    }

    String fileKey = buildFileKey(directory, fileName);
    PutObjectRequest objectRequest =
        PutObjectRequest.builder()
            .bucket(property.bucket())
            .key(fileKey)
            .contentType(getContentTypeFromFileName(fileName))
            .build();
    PutObjectPresignRequest presignRequest =
        PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(5))
            .putObjectRequest(objectRequest)
            .build();
    return s3Presigner.presignPutObject(presignRequest).url().toString();
  }

  public String getOriginalUrl(String presignedUrl) {
    try {
      if (isCompleteS3Url(presignedUrl) && !presignedUrl.contains("?")) {
        return presignedUrl;
      }

      URI uri = new URI(presignedUrl);
      String key = resolveKey(uri);
      if (key.startsWith(property.bucket() + "/")) {
        key = key.substring(property.bucket().length() + 1);
      }
      return getFileUrl(key);
    } catch (URISyntaxException e) {
      throw new S3Exception(S3Failure.INVALID_FILE_URL);
    }
  }

  private void validateContentType(String contentType) {
    if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
      throw new S3Exception(S3Failure.INVALID_CONTENT_TYPE);
    }
  }

  private String buildFileKey(String directory, String fileName) {
    return property.baseDir() + resolveDirectory(directory) + fileName;
  }

  private String getFileUrl(String fileKey) {
    return String.format(
        "https://s3.%s.amazonaws.com/%s/%s", property.region(), property.bucket(), fileKey);
  }

  private boolean isCompleteS3Url(String url) {
    if (url == null || url.isBlank()) {
      return false;
    }
    return (url.startsWith("https://s3.") && url.contains(".amazonaws.com/"))
        || url.startsWith("https://" + property.bucket() + ".s3.");
  }

  private String extractKey(String fileUrl) {
    try {
      return resolveKey(new URI(fileUrl));
    } catch (Exception e) {
      throw new S3Exception(S3Failure.INVALID_FILE_URL);
    }
  }

  private String resolveKey(URI uri) {
    String path = uri.getPath();
    if (path == null || path.isBlank()) {
      throw new S3Exception(S3Failure.INVALID_FILE_URL);
    }

    String key = path.startsWith("/") ? path.substring(1) : path;
    if (key.startsWith(property.bucket() + "/")) {
      return key.substring(property.bucket().length() + 1);
    }
    return key;
  }

  private String createFileName(String originalFileName) {
    return UUID.randomUUID() + "_" + originalFileName;
  }

  private String getContentTypeFromFileName(String fileName) {
    String extension = getFileExtension(fileName);
    return CONTENT_TYPES_BY_EXTENSION.getOrDefault(extension, "application/octet-stream");
  }

  private String getFileExtension(String fileName) {
    int lastDotIndex = fileName.lastIndexOf(".");
    return lastDotIndex == -1 ? "" : fileName.substring(lastDotIndex + 1).toLowerCase();
  }

  private String resolveDirectory(String directory) {
    if (directory == null || directory.trim().isEmpty()) {
      return "";
    }
    String resolved = directory.trim();
    if (resolved.startsWith("/")) {
      resolved = resolved.substring(1);
    }
    return resolved.endsWith("/") ? resolved : resolved + "/";
  }
}
