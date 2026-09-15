package org.sopt.makers.domain.app.soptamp.stamp.port;

import java.util.Collection;

public interface StampFileStoragePort {

  PresignedFile generatePresignedUrl(PresignedFileRequest request);

  void deleteAll(Collection<String> fileUrls);

  record PresignedFileRequest(String fileName, String contentType, String directory) {}

  record PresignedFile(String presignedUrl, String fileUrl, long expiresIn, String fileKey) {}
}
