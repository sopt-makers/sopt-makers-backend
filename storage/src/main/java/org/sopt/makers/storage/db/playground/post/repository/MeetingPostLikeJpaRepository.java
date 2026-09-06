package org.sopt.makers.storage.db.playground.post.repository;

import java.util.List;
import org.sopt.makers.storage.db.playground.post.entity.MeetingPostLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingPostLikeJpaRepository extends JpaRepository<MeetingPostLikeEntity, Long> {

  boolean existsByPostIdAndUserId(Long postId, Long userId);

  void deleteByPostIdAndUserId(Long postId, Long userId);

  List<MeetingPostLikeEntity> findAllByPostIdInAndUserId(List<Long> postIds, Long userId);

  void deleteAllByUserId(Long userId);
}
