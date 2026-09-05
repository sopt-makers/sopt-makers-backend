package org.sopt.makers.storage.db.playground.post.repository;

import java.util.List;
import org.sopt.makers.storage.db.playground.post.entity.PostLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeJpaRepository extends JpaRepository<PostLikeEntity, Long> {

  boolean existsByPostIdAndUserId(Long postId, Long userId);

  void deleteByPostIdAndUserId(Long postId, Long userId);

  List<PostLikeEntity> findAllByPostIdInAndUserId(List<Long> postIds, Long userId);

  void deleteAllByUserId(Long userId);
}
