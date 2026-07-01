package org.sopt.makers.domain.admin.banner.port;

import java.io.InputStream;

public interface BannerFileStoragePort {

  String upload(UploadFile file, String directory);

  void delete(String fileUrl);

  String getUrl(String fileUrl);

  record UploadFile(
      String originalFilename, String contentType, long size, InputStream inputStream) {}
}
