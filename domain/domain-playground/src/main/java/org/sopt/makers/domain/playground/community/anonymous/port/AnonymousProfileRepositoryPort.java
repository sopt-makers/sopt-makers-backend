package org.sopt.makers.domain.playground.community.anonymous.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfile;

public interface AnonymousProfileRepositoryPort {

  AnonymousProfile save(AnonymousProfile profile);

  Optional<AnonymousProfile> findByUserIdAndPostId(Long userId, Long postId);

  List<AnonymousProfile> findAllByPostId(Long postId);

  List<AnonymousProfile> findRecentOrderByCreatedAtDesc(int limit);

  List<String> findNicknamesByPostIdAndNicknamesIn(Long postId, List<String> nicknames);

  List<AnonymousProfile> findByPostIdAndNicknamesIn(Long postId, List<String> nicknames);

  Optional<AnonymousProfile> findById(Long id);

  List<AnonymousProfile> findAllByIds(List<Long> ids);
}
