package org.sopt.makers.storage.db.admin.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.LectureAttendanceDetail;
import org.sopt.makers.domain.admin.attendance.MemberAttendanceDetail;
import org.sopt.makers.domain.admin.lecture.LectureAttribute;
import org.sopt.makers.domain.admin.lecture.LectureStatus;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.storage.db.admin.entity.AttendanceEntity;
import org.sopt.makers.storage.db.admin.entity.LectureEntity;
import org.sopt.makers.storage.db.admin.entity.SubAttendanceEntity;
import org.sopt.makers.storage.db.admin.entity.SubLectureEntity;
import org.sopt.makers.storage.db.user.entity.UserActivityHistoryEntity;
import org.sopt.makers.storage.db.user.entity.UserEntity;
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
              assertThat(detail.lectureName()).isEqualTo("세미나");
              assertThat(detail.status()).isEqualTo(AttendanceStatus.ABSENT);
              assertThat(detail.additiveScore()).isEqualTo(-1f);
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
        .extracting(MemberAttendanceDetail::lectureName)
        .containsExactlyInAnyOrder("첫 세션", "둘째 세션");
    assertThat(result)
        .filteredOn(detail -> detail.lectureName().equals("첫 세션"))
        .singleElement()
        .satisfies(
            detail ->
                assertThat(detail.subAttendances())
                    .containsExactlyInAnyOrder(firstRound, secondRound));
    assertThat(result)
        .filteredOn(detail -> detail.lectureName().equals("둘째 세션"))
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
    saveAttendance(10L, "현재 기수 세션", 39);
    saveAttendance(20L, "다른 사용자 세션", 39);
    flushAndClear();

    var result = adapter.findAllDetailsByUserIdAndGeneration(10L, 39);

    assertThat(result).extracting(MemberAttendanceDetail::lectureName).containsExactly("현재 기수 세션");
  }

  @Test
  @DisplayName("세션 시작 시간이 최근인 출석부터 내림차순으로 반환한다")
  void ordersByLectureStartDateDescending() {
    saveAttendance(10L, "이른 세션", 39, START);
    saveAttendance(10L, "최근 세션", 39, START.plusDays(2));
    saveAttendance(10L, "중간 세션", 39, START.plusDays(1));
    flushAndClear();

    var result = adapter.findAllDetailsByUserIdAndGeneration(10L, 39);

    assertThat(result)
        .extracting(MemberAttendanceDetail::lectureName)
        .containsExactly("최근 세션", "중간 세션", "이른 세션");
  }

  @Test
  @DisplayName("전체 세션의 출석도 회원의 해당 기수 파트로 필터링한다")
  void filtersLectureAttendanceByMemberPart() {
    var kim =
        entityManager.persist(
            UserEntity.create("김솝트", "01000000001", null, null, null, "kim", OAuthPlatform.GOOGLE));
    var hong =
        entityManager.persist(
            UserEntity.create(
                "홍길동", "01000000002", null, null, null, "hong", OAuthPlatform.GOOGLE));
    entityManager.persist(
        UserActivityHistoryEntity.create(kim, 39, null, Part.WEB, Role.MEMBER, true, 2.0f));
    entityManager.persist(
        UserActivityHistoryEntity.create(hong, 39, null, Part.SERVER, Role.MEMBER, true, 1.5f));
    entityManager.persist(
        UserActivityHistoryEntity.create(hong, 38, null, Part.WEB, Role.MEMBER, true, 1.0f));
    var lecture =
        entityManager.persist(
            LectureEntity.builder()
                .name("전체 세미나")
                .part(Part.ALL)
                .generation(39)
                .startDate(START)
                .endDate(START.plusHours(4))
                .attribute(LectureAttribute.SEMINAR)
                .status(LectureStatus.END)
                .build());
    entityManager.persist(AttendanceEntity.create(kim.getId(), lecture));
    var attendance = entityManager.persist(AttendanceEntity.create(hong.getId(), lecture));
    var sub = saveSubAttendance(attendance, 1, AttendanceStatus.TARDY);
    flushAndClear();
    var result = adapter.findAllByLectureIdAndPart(lecture.getId(), Part.SERVER, 0, 20);
    assertThat(result)
        .singleElement()
        .satisfies(
            detail -> {
              assertThat(detail.memberId()).isEqualTo(hong.getId());
              assertThat(detail.memberName()).isEqualTo("홍길동");
              assertThat(detail.updatedScore()).isEqualTo(-1.0f);
              assertThat(detail.attendances())
                  .singleElement()
                  .satisfies(
                      item -> {
                        assertThat(item.status()).isEqualTo(AttendanceStatus.TARDY);
                        assertThat(item.updatedAt()).isEqualTo(sub.updatedAt());
                      });
            });
    assertThat(adapter.countByLectureIdAndPart(lecture.getId(), Part.SERVER)).isEqualTo(1);
    assertThat(adapter.findAllByLectureIdAndPart(lecture.getId(), Part.ALL, 0, 1))
        .extracting(LectureAttendanceDetail::memberName)
        .containsExactly("김솝트");
    assertThat(adapter.findAllByLectureIdAndPart(lecture.getId(), null, 1, 1))
        .extracting(LectureAttendanceDetail::memberName)
        .containsExactly("홍길동");
    assertThat(adapter.countByLectureIdAndPart(lecture.getId(), null)).isEqualTo(2);
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

  private MemberAttendanceDetail.SubAttendanceDetail saveSubAttendance(
      AttendanceEntity attendance, int round, AttendanceStatus status) {
    SubLectureEntity lecture = SubLectureEntity.create(attendance.getLecture(), round);
    lecture.updateCodeAndStartAt("1234", START);
    entityManager.persist(lecture);
    SubAttendanceEntity sub = SubAttendanceEntity.create(attendance, lecture);
    sub.updateStatus(status);
    sub.updateAttendedAt(START.plusMinutes(1));
    entityManager.persistAndFlush(sub);
    entityManager.refresh(sub);
    return new MemberAttendanceDetail.SubAttendanceDetail(round, status, sub.getUpdatedAt());
  }

  private void flushAndClear() {
    entityManager.flush();
    entityManager.clear();
  }
}
