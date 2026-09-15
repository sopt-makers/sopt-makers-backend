package org.sopt.makers.storage.db.playground.member.profile.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.review.service.ReviewService;
import org.sopt.makers.domain.playground.member.profile.port.OfficialReviewCountPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** domain-playground(member)와 domain-official(review) 간 모듈 경계를 이어주는 브릿지 어댑터. */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OfficialReviewCountAdapter implements OfficialReviewCountPort {

  private final ReviewService reviewService;

  @Override
  public int countReviewsByAuthor(String author) {
    return reviewService.getReviewsByAuthor(author).reviewCount();
  }
}
