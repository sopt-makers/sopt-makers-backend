package org.sopt.makers.domain.app.soptamp.rank.port;

import java.util.List;
import org.sopt.makers.domain.app.soptamp.rank.RankedScore;

public interface RankCachePort {

  List<RankedScore> getRanking();

  void putAll(List<RankedScore> scores);

  void updateScore(Long userId, long score);

  void removeScore(Long userId);

  void clearScores();
}
