package org.sopt.makers.storage.redis.admin.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sopt.makers.domain.official.admin.port.AdminCachePort;
import org.sopt.makers.domain.official.faq.FaqCategory;
import org.sopt.makers.domain.official.homepage.review.HomepageReviewType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import tools.jackson.databind.json.JsonMapper;

class AdminOfficialCacheRepositoryAdapterTest {
  private final StringRedisTemplate template = mock(StringRedisTemplate.class);
  private final ValueOperations<String, String> values = mock();
  private final AdminOfficialCacheRepositoryAdapter adapter =
      new AdminOfficialCacheRepositoryAdapter(template, JsonMapper.builder().build());

  @BeforeEach
  void setUp() {
    when(template.opsForValue()).thenReturn(values);
  }

  @Test
  @DisplayName("후기 유형은 캐시에 저장하고 읽어도 유지된다")
  void roundTripsReviewTypes() {
    var data =
        new AdminCachePort.HomeCacheData(
            39,
            null,
            null,
            List.of(
                new AdminCachePort.ReviewData("후기", "내용", "작성자", HomepageReviewType.RECRUITING)));
    adapter.putHome(39, data);
    var json = ArgumentCaptor.forClass(String.class);
    verify(values).set(eq("admin:official:home:39"), json.capture(), eq(Duration.ofHours(1)));
    when(values.get("admin:official:home:39")).thenReturn(json.getValue());
    assertThat(adapter.getHome(39)).isEqualTo(data);
  }

  @Test
  @DisplayName("카테고리 FAQ는 캐시에 저장하고 읽어도 유지된다")
  void roundTripsCategoryFaqs() {
    var data =
        new AdminCachePort.RecruitCacheData(
            39,
            null,
            null,
            null,
            null,
            List.of(
                new AdminCachePort.FaqData(
                    FaqCategory.QUALIFICATION,
                    List.of(new AdminCachePort.QuestionData("질문", "답변")))));
    adapter.putRecruit(39, data);
    var json = ArgumentCaptor.forClass(String.class);
    verify(values).set(eq("admin:official:recruit:39"), json.capture(), eq(Duration.ofHours(1)));
    when(values.get("admin:official:recruit:39")).thenReturn(json.getValue());
    assertThat(adapter.getRecruit(39)).isEqualTo(data);
  }
}
