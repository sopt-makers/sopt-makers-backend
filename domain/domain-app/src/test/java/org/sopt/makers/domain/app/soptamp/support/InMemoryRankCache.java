package org.sopt.makers.domain.app.soptamp.support;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.sopt.makers.domain.app.soptamp.rank.RankedScore;
import org.sopt.makers.domain.app.soptamp.rank.port.RankCachePort;

public final class InMemoryRankCache implements RankCachePort {

  private final Map<Long, Long> scores = new LinkedHashMap<>();
  private final List<String> calls = new ArrayList<>();

  public List<String> calls() {
    return List.copyOf(calls);
  }

  @Override
  public List<RankedScore> getRanking() {
    return scores.entrySet().stream()
        .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
        .map(entry -> new RankedScore(entry.getKey(), entry.getValue()))
        .toList();
  }

  @Override
  public void putAll(List<RankedScore> newScores) {
    calls.add("putAll");
    newScores.forEach(score -> scores.put(score.userId(), score.score()));
  }

  @Override
  public void updateScore(Long userId, long score) {
    calls.add("updateScore");
    scores.put(userId, score);
  }

  @Override
  public void removeScore(Long userId) {
    calls.add("removeScore");
    scores.remove(userId);
  }

  @Override
  public void clearScores() {
    calls.add("clearScores");
    scores.clear();
  }

  public void put(Long userId, long score) {
    scores.put(userId, score);
  }
}
