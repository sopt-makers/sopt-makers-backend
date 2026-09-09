package org.sopt.makers.storage.db.playground.community.post.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.CommunityCategoryGroup;
import org.sopt.makers.domain.playground.community.post.Post;
import org.sopt.makers.domain.playground.community.post.port.PostRepositoryPort;
import org.sopt.makers.storage.db.playground.community.entity.CategoryEntity;
import org.sopt.makers.storage.db.playground.community.post.entity.PostEntity;
import org.sopt.makers.storage.db.playground.community.post.repository.PostJpaRepository;
import org.sopt.makers.storage.db.playground.community.repository.CategoryJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostRepositoryAdapter implements PostRepositoryPort {

  private final PostJpaRepository postJpaRepository;
  private final CategoryJpaRepository categoryJpaRepository;

  @Transactional
  @Override
  public Post save(Post post) {
    CategoryEntity category = categoryJpaRepository.getReferenceById(post.categoryId());

    if (post.id() == null) {
      return postJpaRepository.save(PostEntity.of(post, category)).toDomain();
    }

    PostEntity entity =
        postJpaRepository
            .findById(post.id())
            .orElseThrow(() -> new IllegalStateException("존재하지 않는 게시글입니다. id: " + post.id()));
    entity.applyChanges(post, category);
    return postJpaRepository.saveAndFlush(entity).toDomain();
  }

  @Transactional
  @Override
  public void delete(Post post) {
    postJpaRepository.deleteById(post.id());
  }

  @Override
  public boolean existsById(Long postId) {
    return postJpaRepository.existsById(postId);
  }

  @Override
  public Optional<Post> findById(Long postId) {
    return postJpaRepository.findById(postId).map(PostEntity::toDomain);
  }

  @Override
  public Optional<Post> findByIdWithCategory(Long postId) {
    return postJpaRepository.findByIdWithCategory(postId).map(PostEntity::toDomain);
  }

  @Override
  public List<Post> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
    return postJpaRepository.findAllByCreatedAtBetween(start, end).stream()
        .map(PostEntity::toDomain)
        .toList();
  }

  @Override
  public Integer countAllByWriterIdAndCreatedAtBetween(
      Long writerId, LocalDateTime start, LocalDateTime end) {
    return postJpaRepository.countAllByWriterIdAndCreatedAtBetween(writerId, start, end);
  }

  @Transactional
  @Override
  public void increaseHits(Long postId) {
    postJpaRepository.increaseHitsDirect(postId);
  }

  @Override
  public List<Post> findTop5ByCategoryCodesOrderByCreatedAtDesc(
      List<CommunityCategoryCode> categoryCodes) {
    return postJpaRepository.findTop5ByCategory_CodeInOrderByCreatedAtDesc(categoryCodes).stream()
        .map(PostEntity::toDomain)
        .toList();
  }

  @Override
  public List<Post> findTop3ByCategoryGroupsOrderByCreatedAtDesc(
      List<CommunityCategoryGroup> categoryGroups) {
    return postJpaRepository
        .findTop3ByCategory_CategoryGroupInOrderByCreatedAtDesc(categoryGroups)
        .stream()
        .map(PostEntity::toDomain)
        .toList();
  }

  @Override
  public Optional<Post> findFirstByCategoryCodesOrderByCreatedAtDesc(
      List<CommunityCategoryCode> categoryCodes) {
    return postJpaRepository
        .findFirstByCategory_CodeInOrderByCreatedAtDesc(categoryCodes)
        .map(PostEntity::toDomain);
  }

  @Override
  public long countSopticleByWriterId(Long writerId) {
    return postJpaRepository.countSopticleByWriterId(writerId);
  }

  @Override
  public List<Post> findByCategoryCodesWithCursor(
      List<CommunityCategoryCode> categoryCodes,
      LocalDateTime cursorCreatedAt,
      Long cursorPostId,
      LocalDateTime snapshotTime,
      int limit,
      Set<Long> excludedWriterIds) {
    return postJpaRepository
        .findByCategoryCodesWithCursor(
            categoryCodes, cursorCreatedAt, cursorPostId, snapshotTime, limit, excludedWriterIds)
        .stream()
        .map(PostEntity::toDomain)
        .toList();
  }

  @Override
  public Optional<Post> findMostRecentHotPost() {
    return postJpaRepository.findFirstByIsHotTrueOrderByCreatedAtDesc().map(PostEntity::toDomain);
  }

  @Transactional
  @Override
  public void markAsHot(Long postId) {
    postJpaRepository.updateIsHotByPostId(postId);
  }

  @Override
  public List<Post> findPopularPosts(int limitCount) {
    LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
    return postJpaRepository
        .findByCreatedAtAfterOrderByHitsDesc(oneMonthAgo, PageRequest.of(0, limitCount))
        .stream()
        .map(PostEntity::toDomain)
        .toList();
  }

  @Override
  public List<Post> findPopularCandidatePosts(LocalDateTime since) {
    return postJpaRepository
        .findByCreatedAtGreaterThanEqualAndIsReportedFalseOrderByCreatedAtDescIdDesc(since)
        .stream()
        .map(PostEntity::toDomain)
        .toList();
  }
}
