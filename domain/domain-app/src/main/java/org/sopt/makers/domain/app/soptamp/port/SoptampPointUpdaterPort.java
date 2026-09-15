package org.sopt.makers.domain.app.soptamp.port;

public interface SoptampPointUpdaterPort {

  void addPointByLevel(Long userId, int level);

  void subtractPointByLevel(Long userId, int level);

  void initPoint(Long userId);

  void initAllPoints();
}
