package org.sopt.makers.domain.official.news.port;

import java.io.InputStream;

public interface NewsFileStoragePort {

  String upload(UploadFile file, String directory);

  void delete(String fileUrl);

  PresignedFile generatePresignedUrl(PresignedFileRequest request);

  record UploadFile(
      String originalFilename, String contentType, long size, InputStream inputStream) {}

  record PresignedFileRequest(String fileName, String contentType, String directory) {}

  record PresignedFile(String presignedUrl, String fileUrl, long expiresIn, String fileKey) {}
}
