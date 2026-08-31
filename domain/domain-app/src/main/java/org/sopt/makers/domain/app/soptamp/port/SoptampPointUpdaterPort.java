package org.sopt.makers.domain.app.soptamp.port;

public interface SoptampPointUpdaterPort {

  void addPointByLevel(Long userId, Integer level);

  void subtractPointByLevel(Long userId, Integer level);

  void initPoint(Long userId);

  void initAllPoints();
}
