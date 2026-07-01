package org.sopt.makers.domain.official.admin.port;

public interface AdminFileStoragePort {

  String generatePresignedUrl(String fileName, String directory);

  String getOriginalUrl(String presignedUrl);
}
