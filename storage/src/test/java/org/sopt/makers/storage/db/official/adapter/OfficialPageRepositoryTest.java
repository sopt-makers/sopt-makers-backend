package org.sopt.makers.storage.db.official.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.official.faq.*;
import org.sopt.makers.domain.official.homepage.review.*;
import org.sopt.makers.domain.official.news.News;
import org.sopt.makers.storage.db.official.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest(properties = "spring.jpa.properties.hibernate.auto_quote_keyword=true")
@ContextConfiguration(classes = OfficialPageRepositoryTest.Config.class)
@Import(FaqRepositoryAdapter.class)
class OfficialPageRepositoryTest {
  @Autowired private TestEntityManager em;
  @Autowired private FaqRepositoryAdapter faqs;

  @Test
  @DisplayName("FAQ 전체 삭제 시 파트와 카테고리 FAQ를 모두 삭제한다")
  void deletesAllFaqs() {
    faqs.saveAll(
        List.of(
            new Faq(null, FaqCategory.WEB, List.of(new Faq.QuestionAnswer("질문", "답변"))),
            new Faq(null, FaqCategory.QUALIFICATION, List.of())));
    em.flush();
    em.clear();
    faqs.deleteAll();
    em.flush();
    em.clear();
    assertThat(faqs.findAll()).isEmpty();
  }

  @Test
  @DisplayName("후기 유형을 저장하고 조회한다")
  void persistsReviewType() {
    var entity =
        em.persistAndFlush(
            HomepageReviewEntity.fromDomain(
                new HomepageReview(
                    null, HomepageReviewType.RECRUITING, "후기", "내용", "작성자", null, null)));
    em.clear();
    assertThat(em.find(HomepageReviewEntity.class, entity.getId()).toDomain().type())
        .isEqualTo(HomepageReviewType.RECRUITING);
  }

  @Test
  @DisplayName("소식의 게시일과 숨김 여부를 저장하고 조회한다")
  void persistsNewsMetadata() {
    var time = Instant.parse("2026-01-01T16:00:00Z");
    var entity =
        em.persistAndFlush(
            NewsEntity.fromDomain(new News(null, "image", "소식", "link", time, true, null, null)));
    em.clear();
    var news = em.find(NewsEntity.class, entity.getId()).toDomain();
    assertThat(news.publishedAt()).isEqualTo(time);
    assertThat(news.hidden()).isTrue();
    assertThat(news.publishedDate()).isEqualTo("2026.01.02");
  }

  @Configuration
  @EntityScan(basePackages = "org.sopt.makers.storage.db.official.entity")
  @EnableJpaRepositories(basePackages = "org.sopt.makers.storage.db.official.repository")
  static class Config {}
}
