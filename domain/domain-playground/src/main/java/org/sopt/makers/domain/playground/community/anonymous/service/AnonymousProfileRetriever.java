package org.sopt.makers.domain.playground.community.anonymous.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfile;
import org.sopt.makers.domain.playground.community.anonymous.port.AnonymousProfileRepositoryPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnonymousProfileRetriever {

  private final AnonymousProfileRepositoryPort anonymousProfileRepositoryPort;

  public Optional<AnonymousProfile> findByUserIdAndPostId(Long userId, Long postId) {
    return anonymousProfileRepositoryPort.findByUserIdAndPostId(userId, postId);
  }

  public List<AnonymousProfile> findAllByPostId(Long postId) {
    return anonymousProfileRepositoryPort.findAllByPostId(postId);
  }

  public List<AnonymousProfile> getRecentProfiles(int limit) {
    return anonymousProfileRepositoryPort.findRecentOrderByCreatedAtDesc(limit);
  }

  public List<String> findNicknamesByPostIdAndNicknamesIn(Long postId, List<String> nicknames) {
    return anonymousProfileRepositoryPort.findNicknamesByPostIdAndNicknamesIn(postId, nicknames);
  }

  public List<AnonymousProfile> findByPostIdAndNicknames(Long postId, List<String> nicknames) {
    if (nicknames == null || nicknames.isEmpty()) {
      return Collections.emptyList();
    }
    return anonymousProfileRepositoryPort.findByPostIdAndNicknamesIn(postId, nicknames);
  }

  public Long[] extractUserIds(List<AnonymousProfile> profiles) {
    if (profiles == null || profiles.isEmpty()) {
      return new Long[0];
    }
    return profiles.stream().map(AnonymousProfile::userId).distinct().toArray(Long[]::new);
  }
}
