package org.sopt.makers.domain.crew.advertisement.port;

import java.io.InputStream;

public interface AdvertisementImageStoragePort {

  String upload(UploadImage image, String directory);

  record UploadImage(
      String originalFilename, String contentType, long size, InputStream inputStream) {}
}
