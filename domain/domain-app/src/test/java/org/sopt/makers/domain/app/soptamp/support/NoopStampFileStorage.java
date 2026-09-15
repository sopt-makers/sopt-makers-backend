package org.sopt.makers.domain.app.soptamp.support;

import java.util.Collection;
import org.sopt.makers.domain.app.soptamp.stamp.port.StampFileStoragePort;

public final class NoopStampFileStorage implements StampFileStoragePort {

  @Override
  public PresignedFile generatePresignedUrl(PresignedFileRequest request) {
    return null;
  }

  @Override
  public void deleteAll(Collection<String> fileUrls) {}
}
