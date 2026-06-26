package org.sopt.makers.domain.official.soptstory.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.official.soptstory.SoptStory;
import org.sopt.makers.domain.official.soptstory.SoptStoryLike;

public interface SoptStoryLikeRepositoryPort {

  boolean existsBySoptStoryIdAndIp(Long soptStoryId, String ip);

  Optional<SoptStoryLike> findBySoptStoryIdAndIp(Long soptStoryId, String ip);

  List<SoptStoryLike> findAllByIpAndSoptStoryIn(String ip, List<SoptStory> soptStories);

  SoptStoryLike save(SoptStoryLike like);

  void delete(SoptStoryLike like);
}
