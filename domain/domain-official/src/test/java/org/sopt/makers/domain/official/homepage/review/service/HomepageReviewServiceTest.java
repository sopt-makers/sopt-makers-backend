package org.sopt.makers.domain.official.homepage.review.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.sopt.makers.domain.official.homepage.review.HomepageReviewType.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.official.homepage.review.*;
import org.sopt.makers.domain.official.homepage.review.exception.HomepageReviewException;
import org.sopt.makers.domain.official.homepage.review.port.HomepageReviewRepositoryPort;

class HomepageReviewServiceTest {
  private final HomepageReviewRepositoryPort port = mock(HomepageReviewRepositoryPort.class);
  private final HomepageReviewService service = new HomepageReviewService(port);

  @BeforeEach
  void setUp() {
    when(port.save(any())).thenAnswer(inv -> inv.getArgument(0));
  }

  @Test
  @DisplayName("유형을 생략하면 활동 후기를 생성한다")
  void defaultsType() {
    var result =
        service.create(
            new HomepageReviewService.CreateHomepageReviewCommand("제목", "내용", "작성자", null));
    assertThat(result.type()).isEqualTo(ACTIVITY);
    verify(port).countByType(ACTIVITY);
  }

  @Test
  @DisplayName("지원 후기는 활동 후기 개수와 별개로 생성한다")
  void createsRecruitingReview() {
    when(port.countByType(ACTIVITY)).thenReturn(7L);
    var result =
        service.create(
            new HomepageReviewService.CreateHomepageReviewCommand("제목", "내용", "작성자", RECRUITING));
    assertThat(result.type()).isEqualTo(RECRUITING);
    verify(port).countByType(RECRUITING);
  }

  @Test
  @DisplayName("같은 유형의 후기가 7개면 추가할 수 없다")
  void rejectsFullType() {
    when(port.countByType(ACTIVITY)).thenReturn(7L);
    assertThatThrownBy(
            () ->
                service.create(
                    new HomepageReviewService.CreateHomepageReviewCommand(
                        "제목", "내용", "작성자", ACTIVITY)))
        .isInstanceOf(HomepageReviewException.class);
    verify(port, never()).save(any());
  }

  @Test
  @DisplayName("수정 시 유형을 생략하면 기존 유형을 유지한다")
  void preservesType() {
    when(port.findById(1L)).thenReturn(Optional.of(review(RECRUITING)));
    var result =
        service.edit(
            1L, new HomepageReviewService.EditHomepageReviewCommand("수정", "내용", "작성자", null));
    assertThat(result.type()).isEqualTo(RECRUITING);
    assertThat(result.title()).isEqualTo("수정");
    verify(port, never()).countByType(any());
  }

  @Test
  @DisplayName("같은 유형을 유지하는 수정에는 등록 개수 제한을 적용하지 않는다")
  void allowsSameTypeEdit() {
    when(port.findById(1L)).thenReturn(Optional.of(review(ACTIVITY)));
    assertThat(
            service
                .edit(
                    1L,
                    new HomepageReviewService.EditHomepageReviewCommand(
                        "수정", "내용", "작성자", ACTIVITY))
                .type())
        .isEqualTo(ACTIVITY);
    verify(port, never()).countByType(any());
  }

  @Test
  @DisplayName("다른 유형으로 변경할 때 해당 유형에 여유가 있으면 수정한다")
  void changesType() {
    when(port.findById(1L)).thenReturn(Optional.of(review(ACTIVITY)));
    assertThat(
            service
                .edit(
                    1L,
                    new HomepageReviewService.EditHomepageReviewCommand(
                        "수정", "내용", "작성자", RECRUITING))
                .type())
        .isEqualTo(RECRUITING);
    verify(port).countByType(RECRUITING);
  }

  @Test
  @DisplayName("변경할 유형의 후기가 7개면 유형을 변경할 수 없다")
  void rejectsTypeChangeToFullType() {
    when(port.findById(1L)).thenReturn(Optional.of(review(ACTIVITY)));
    when(port.countByType(RECRUITING)).thenReturn(7L);
    assertThatThrownBy(
            () ->
                service.edit(
                    1L,
                    new HomepageReviewService.EditHomepageReviewCommand(
                        "수정", "내용", "작성자", RECRUITING)))
        .isInstanceOf(HomepageReviewException.class);
    verify(port, never()).save(any());
  }

  @Test
  @DisplayName("전체 후기가 7개를 넘어도 목록을 조회한다")
  void readsAllTypes() {
    var reviews =
        IntStream.range(0, 14).mapToObj(i -> review(i < 7 ? ACTIVITY : RECRUITING)).toList();
    when(port.findAllByOrderByIdAsc()).thenReturn(reviews);
    assertThat(service.getReviews()).containsExactlyElementsOf(reviews);
  }

  @Test
  @DisplayName("존재하지 않는 후기는 수정할 수 없다")
  void rejectsMissingReview() {
    assertThatThrownBy(
            () ->
                service.edit(
                    1L,
                    new HomepageReviewService.EditHomepageReviewCommand("수정", "내용", "작성자", null)))
        .isInstanceOf(HomepageReviewException.class);
    verify(port, never()).save(any());
  }

  private HomepageReview review(HomepageReviewType type) {
    return new HomepageReview(1L, type, "제목", "내용", "작성자", null, null);
  }

  @Test
  @DisplayName("후기를 일괄 저장할 때 유형을 유지하고 생략한 유형은 활동 후기로 저장한다")
  void savesReviewTypesInBulk() {
    service.createAll(
        new HomepageReviewService.BulkCreateHomepageReviewsCommand(
            List.of(
                new HomepageReviewService.BulkCreateHomepageReviewsCommand.ReviewData(
                    "활동", "내용", "작성자", null),
                new HomepageReviewService.BulkCreateHomepageReviewsCommand.ReviewData(
                    "지원", "내용", "작성자", RECRUITING))));
    org.mockito.ArgumentCaptor<List<HomepageReview>> captor = org.mockito.ArgumentCaptor.captor();
    verify(port).saveAll(captor.capture());
    assertThat(captor.getValue())
        .extracting(HomepageReview::type)
        .containsExactly(ACTIVITY, RECRUITING);
  }

  @Test
  @DisplayName("일괄 저장에서도 유형별 7개 제한을 적용한다")
  void validatesBulkCapacity() {
    var entry =
        new HomepageReviewService.BulkCreateHomepageReviewsCommand.ReviewData(
            "후기", "내용", "작성자", ACTIVITY);
    assertThatThrownBy(
            () ->
                service.createAll(
                    new HomepageReviewService.BulkCreateHomepageReviewsCommand(
                        java.util.Collections.nCopies(8, entry))))
        .isInstanceOf(HomepageReviewException.class);
    verify(port, never()).saveAll(any());
  }
}
