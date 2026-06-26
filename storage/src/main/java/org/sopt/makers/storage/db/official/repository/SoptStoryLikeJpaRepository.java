package org.sopt.makers.storage.db.official.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.official.entity.SoptStoryEntity;
import org.sopt.makers.storage.db.official.entity.SoptStoryLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoptStoryLikeJpaRepository extends JpaRepository<SoptStoryLikeEntity, Long> {

  boolean existsBySoptStory_IdAndIp(Long soptStoryId, String ip);

  Optional<SoptStoryLikeEntity> findBySoptStory_IdAndIp(Long soptStoryId, String ip);

  @Query("SELECT sl FROM SoptStoryLikeEntity sl WHERE sl.ip = :ip AND sl.soptStory IN :soptStories")
  List<SoptStoryLikeEntity> findAllByIpAndSoptStoryIn(
      @Param("ip") String ip, @Param("soptStories") List<SoptStoryEntity> soptStories);
}
