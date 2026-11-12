package org.sopt.makers.storage.db.admin.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.AttendanceDetail;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.lecture.LectureAttribute;
import org.sopt.makers.domain.admin.lecture.LectureStatus;
import org.sopt.makers.storage.db.admin.entity.AttendanceEntity;
import org.sopt.makers.storage.db.admin.entity.LectureEntity;
import org.sopt.makers.storage.db.admin.entity.SubAttendanceEntity;
import org.sopt.makers.storage.db.admin.entity.SubLectureEntity;
import org.sopt.makers.storage.support.AdminJpaTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@ContextConfiguration(classes = AdminJpaTestConfig.class)
@Import(AttendanceRepositoryAdapter.class)
class AttendanceRepositoryAdapterTest {
  private static final LocalDateTime START = LocalDateTime.of(2026, 10, 1, 14, 0);

  @Autowired private TestEntityManager entityManager;
  @Autowired private AttendanceRepositoryAdapter adapter;

  @Test
  @DisplayName("세션 시간과 세부 출석의 상태 및 마지막 수정 시각을 반환한다")
  void mapsLectureAndSubAttendanceDetails() {
    AttendanceEntity attendance = saveAttendance(10L, "세미나");
    var expected = saveSubAttendance(attendance, 1, AttendanceStatus.ATTENDANCE);
    flushAndClear();

    var result = adapter.findAllDetailsByUserIdAndGeneration(10L, 39);

    assertThat(result)
        .singleElement()
        .satisfies(
            detail -> {
              assertThat(detail.attendance().lectureName()).isEqualTo("세미나");
              assertThat(detail.startDate()).isEqualTo(START);
              assertThat(detail.endDate()).isEqualTo(START.plusHours(4));
              assertThat(detail.subAttendances()).containsExactly(expected);
            });
  }

  @Test
  @DisplayName("요청한 사용자의 출석만 조회하고 세부 출석을 세션별로 묶어 일괄 조회한다")
  void groupsSubAttendancesWithoutAdditionalQueries() {
    AttendanceEntity first = saveAttendance(10L, "첫 세션");
    AttendanceEntity second = saveAttendance(10L, "둘째 세션");
    saveAttendance(20L, "다른 사용자 세션");
    var firstRound = saveSubAttendance(first, 1, AttendanceStatus.ATTENDANCE);
    var secondRound = saveSubAttendance(first, 2, AttendanceStatus.ABSENT);
    var otherSessionRound = saveSubAttendance(second, 1, AttendanceStatus.TARDY);
    flushAndClear();
    var statistics =
        entityManager
            .getEntityManager()
            .getEntityManagerFactory()
            .unwrap(SessionFactory.class)
            .getStatistics();
    statistics.clear();

    var result = adapter.findAllDetailsByUserIdAndGeneration(10L, 39);

    assertThat(result)
        .extracting(detail -> detail.attendance().id())
        .containsExactlyInAnyOrder(first.getId(), second.getId());
    assertThat(result)
        .filteredOn(detail -> detail.attendance().id().equals(first.getId()))
        .singleElement()
        .satisfies(
            detail ->
                assertThat(detail.subAttendances())
                    .containsExactlyInAnyOrder(firstRound, secondRound));
    assertThat(result)
        .filteredOn(detail -> detail.attendance().id().equals(second.getId()))
        .singleElement()
        .satisfies(
            detail -> assertThat(detail.subAttendances()).containsExactly(otherSessionRound));
    assertThat(statistics.getPrepareStatementCount()).isEqualTo(2);
  }

  @Test
  @DisplayName("세부 출석이 없으면 빈 세부 목록을 반환한다")
  void returnsEmptySubAttendances() {
    saveAttendance(10L, "세션");
    flushAndClear();

    var result = adapter.findAllDetailsByUserIdAndGeneration(10L, 39);

    assertThat(result)
        .singleElement()
        .satisfies(
            detail -> {
              assertThat(detail.subAttendances()).isEmpty();
              assertThat(detail.attendance().subAttendances()).isEmpty();
            });
  }

  @Test
  @DisplayName("출석이 없으면 세부 출석 쿼리 없이 빈 목록을 반환한다")
  void skipsSubAttendanceQueryWhenEmpty() {
    var statistics =
        entityManager
            .getEntityManager()
            .getEntityManagerFactory()
            .unwrap(SessionFactory.class)
            .getStatistics();
    statistics.clear();

    assertThat(adapter.findAllDetailsByUserIdAndGeneration(999L, 39)).isEmpty();
    assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
  }

  @Test
  @DisplayName("같은 사용자의 이전 기수 출석은 제외하고 요청한 기수의 출석만 반환한다")
  void excludesAttendancesFromOtherGenerations() {
    saveAttendance(10L, "이전 기수 세션", 38);
    AttendanceEntity current = saveAttendance(10L, "현재 기수 세션", 39);
    saveAttendance(20L, "다른 사용자 세션", 39);
    flushAndClear();

    var result = adapter.findAllDetailsByUserIdAndGeneration(10L, 39);

    assertThat(result)
        .extracting(detail -> detail.attendance().id())
        .containsExactly(current.getId());
  }

  @Test
  @DisplayName("세션 시작 시간이 최근인 출석부터 내림차순으로 반환한다")
  void ordersByLectureStartDateDescending() {
    AttendanceEntity early = saveAttendance(10L, "이른 세션", 39, START);
    AttendanceEntity latest = saveAttendance(10L, "최근 세션", 39, START.plusDays(2));
    AttendanceEntity middle = saveAttendance(10L, "중간 세션", 39, START.plusDays(1));
    flushAndClear();

    var result = adapter.findAllDetailsByUserIdAndGeneration(10L, 39);

    assertThat(result)
        .extracting(detail -> detail.attendance().id())
        .containsExactly(latest.getId(), middle.getId(), early.getId());
  }

  private AttendanceEntity saveAttendance(Long userId, String name) {
    return saveAttendance(userId, name, 39);
  }

  private AttendanceEntity saveAttendance(Long userId, String name, int generation) {
    return saveAttendance(userId, name, generation, START);
  }

  private AttendanceEntity saveAttendance(
      Long userId, String name, int generation, LocalDateTime start) {
    LectureEntity lecture =
        entityManager.persist(
            LectureEntity.builder()
                .name(name)
                .part(Part.SERVER)
                .generation(generation)
                .startDate(start)
                .endDate(start.plusHours(4))
                .attribute(LectureAttribute.SEMINAR)
                .status(LectureStatus.END)
                .build());
    return entityManager.persist(AttendanceEntity.create(userId, lecture));
  }

  private AttendanceDetail.SubAttendanceDetail saveSubAttendance(
      AttendanceEntity attendance, int round, AttendanceStatus status) {
    SubLectureEntity lecture = SubLectureEntity.create(attendance.getLecture(), round);
    lecture.updateCodeAndStartAt("1234", START);
    entityManager.persist(lecture);
    SubAttendanceEntity sub = SubAttendanceEntity.create(attendance, lecture);
    sub.updateStatus(status);
    sub.updateAttendedAt(START.plusMinutes(1));
    entityManager.persistAndFlush(sub);
    entityManager.refresh(sub);
    return new AttendanceDetail.SubAttendanceDetail(round, status, sub.getUpdatedAt());
  }

  private void flushAndClear() {
    entityManager.flush();
    entityManager.clear();
  }
}
