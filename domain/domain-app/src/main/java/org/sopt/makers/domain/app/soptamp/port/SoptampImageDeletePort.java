package org.sopt.makers.domain.app.soptamp.port;

import java.util.List;

public interface SoptampImageDeletePort {

  void deleteAll(List<String> imageUrls);
}
