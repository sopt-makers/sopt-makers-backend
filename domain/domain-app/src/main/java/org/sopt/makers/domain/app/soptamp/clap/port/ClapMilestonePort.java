package org.sopt.makers.domain.app.soptamp.clap.port;

public interface ClapMilestonePort {

  boolean tryMarkFirstHit(Long stampId, int milestone);

  void deleteAll();
}
