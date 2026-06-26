package org.sopt.makers.storage.db.official.adapter;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.official.review.CategoryType;
import org.sopt.makers.domain.official.review.Review;
import org.sopt.makers.domain.official.review.ReviewSearchCondition;
import org.sopt.makers.domain.official.review.port.ReviewRepositoryPort;
import org.sopt.makers.storage.db.official.entity.ReviewEntity;
import org.sopt.makers.storage.db.official.repository.ReviewJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewRepositoryAdapter implements ReviewRepositoryPort {

  private final ReviewJpaRepository reviewJpaRepository;

  @Override
  public boolean existsByUrl(String url) {
    return reviewJpaRepository.existsByUrl(url);
  }

  @Override
  @Transactional
  public Review save(Review review) {
    return reviewJpaRepository.save(ReviewEntity.fromDomain(review)).toDomain();
  }

  @Override
  public List<Review> findAllWithFilters(ReviewSearchCondition condition, long offset, int limit) {
    return filteredReviews(condition).stream().skip(offset).limit(limit).toList();
  }

  @Override
  public long countWithFilters(ReviewSearchCondition condition) {
    return filteredReviews(condition).size();
  }

  @Override
  public Review findRandomReviewByPart(Part part) {
    return reviewJpaRepository.findRandomByPart(part, PageRequest.of(0, 1)).stream()
        .findFirst()
        .map(ReviewEntity::toDomain)
        .orElse(null);
  }

  @Override
  public List<Review> findAllByAuthor(String author) {
    return reviewJpaRepository.findAllByAuthor(author).stream()
        .map(ReviewEntity::toDomain)
        .toList();
  }

  private List<Review> filteredReviews(ReviewSearchCondition condition) {
    return reviewJpaRepository.findAll().stream()
        .map(ReviewEntity::toDomain)
        .filter(review -> matchesCategory(review, condition.category()))
        .filter(review -> matchesActivity(review, condition.category(), condition.activity()))
        .filter(review -> condition.part() == null || review.part() == condition.part())
        .filter(
            review ->
                condition.generation() == null
                    || review.generation().equals(condition.generation()))
        .sorted(
            Comparator.comparing(
                Review::createdAt, Comparator.nullsLast(Comparator.reverseOrder())))
        .toList();
  }

  private boolean matchesCategory(Review review, String category) {
    return CategoryType.fromSafely(category)
        .map(categoryType -> review.category() == categoryType)
        .orElse(true);
  }

  private boolean matchesActivity(Review review, String category, String activity) {
    if (activity == null || activity.isBlank() || "전체".equals(activity)) {
      return true;
    }
    if (!CategoryType.ACTIVITY.getDisplayName().equals(category)) {
      return true;
    }
    return review.subjects().contains(activity);
  }
}
