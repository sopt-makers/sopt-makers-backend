package org.sopt.makers.storage.db.official.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.homepage.review.HomepageReview;
import org.sopt.makers.domain.official.homepage.review.port.HomepageReviewRepositoryPort;
import org.sopt.makers.storage.db.official.entity.HomepageReviewEntity;
import org.sopt.makers.storage.db.official.repository.HomepageReviewJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomepageReviewRepositoryAdapter implements HomepageReviewRepositoryPort {

  private final HomepageReviewJpaRepository homepageReviewJpaRepository;

  @Transactional
  @Override
  public HomepageReview save(HomepageReview review) {
    return homepageReviewJpaRepository.save(HomepageReviewEntity.fromDomain(review)).toDomain();
  }

  @Transactional
  @Override
  public List<HomepageReview> saveAll(List<HomepageReview> reviews) {
    List<HomepageReviewEntity> entities =
        reviews.stream().map(HomepageReviewEntity::fromDomain).toList();
    return homepageReviewJpaRepository.saveAll(entities).stream()
        .map(HomepageReviewEntity::toDomain)
        .toList();
  }

  @Override
  public Optional<HomepageReview> findById(Long id) {
    return homepageReviewJpaRepository.findById(id).map(HomepageReviewEntity::toDomain);
  }

  @Override
  public List<HomepageReview> findAllByOrderByIdAsc() {
    return homepageReviewJpaRepository.findAllByOrderByIdAsc().stream()
        .map(HomepageReviewEntity::toDomain)
        .toList();
  }

  @Transactional
  @Override
  public void deleteAll() {
    homepageReviewJpaRepository.deleteAll();
  }
}
