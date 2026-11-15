package org.sopt.makers.storage.db.admin.adapter;

import static org.assertj.core.api.Assertions.*;

import java.time.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.type.*;
import org.sopt.makers.domain.admin.app.service.AppMemberService;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.service.AttendanceService;
import org.sopt.makers.domain.admin.lecture.*;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.storage.db.admin.entity.*;
import org.sopt.makers.storage.db.user.adapter.AdminUserActivityAdapter;
import org.sopt.makers.storage.db.user.entity.*;
import org.sopt.makers.storage.support.AdminJpaTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest(properties = "sopt.current.generation=39")
@ContextConfiguration(classes = AdminJpaTestConfig.class)
@Import({
  AppLectureRepositoryAdapter.class,
  AppScheduleRepositoryAdapter.class,
  AttendanceRepositoryAdapter.class,
  SubAttendanceRepositoryAdapter.class,
  SubLectureRepositoryAdapter.class,
  AdminUserActivityAdapter.class,
  AttendanceService.class,
  AppMemberService.class
})
class AppOperationRepositoryAdapterTest {
  @Autowired private TestEntityManager em;
  @Autowired private AppLectureRepositoryAdapter lectures;
  @Autowired private AppScheduleRepositoryAdapter schedules;
  @Autowired private AppMemberService members;
  @Autowired private AttendanceService attendanceService;
  private static final LocalDateTime TIME = LocalDateTime.of(2026, 11, 15, 14, 0);

  private UserEntity user() {
    var user =
        em.persist(
            UserEntity.create(
                "홍길동", "01000000001", null, null, null, "test", OAuthPlatform.GOOGLE));
    em.persist(
        UserActivityHistoryEntity.create(user, 39, null, Part.SERVER, Role.MEMBER, true, null));
    em.persist(UserActivityHistoryEntity.create(user, 38, null, Part.WEB, Role.MEMBER, true, 1f));
    return user;
  }

  private LectureEntity lecture(
      String name, int generation, Part part, LectureStatus status, LocalDateTime time) {
    return em.persist(
        LectureEntity.builder()
            .name(name)
            .part(part)
            .generation(generation)
            .startDate(time)
            .endDate(time.plusHours(4))
            .attribute(LectureAttribute.SEMINAR)
            .status(status)
            .build());
  }

  private AttendanceEntity attendance(UserEntity user, LectureEntity lecture) {
    return em.persist(AttendanceEntity.create(user.getId(), lecture));
  }

  @Test
  @DisplayName("오늘 세션은 운영 기수와 회원 파트로 조회하고 실제 출석 시각을 반환한다")
  void readsTodayLectureAndAttendanceTime() {
    var user = user();
    LocalDateTime today = LocalDate.now().atTime(14, 0);
    var lecture = lecture("현재 세미나", 39, Part.SERVER, LectureStatus.FIRST, today);
    var attendance = attendance(user, lecture);
    attendance(user, lecture("이전 기수", 38, Part.SERVER, LectureStatus.FIRST, today));
    attendance(user, lecture("다른 파트", 39, Part.WEB, LectureStatus.FIRST, today));
    attendance(user, lecture("어제", 39, Part.SERVER, LectureStatus.END, today.minusDays(1)));
    var subLecture = SubLectureEntity.create(lecture, 1);
    subLecture.updateCodeAndStartAt("1234", today);
    em.persist(subLecture);
    var sub = SubAttendanceEntity.create(attendance, subLecture);
    sub.updateStatus(AttendanceStatus.ATTENDANCE);
    sub.updateAttendedAt(today.minusHours(2));
    em.persistAndFlush(sub);
    em.refresh(sub);
    var updatedAt = sub.getUpdatedAt();
    em.clear();
    var result = lectures.findTodayLectures(user.getId(), 39, Part.SERVER);
    assertThat(result)
        .singleElement()
        .satisfies(
            item -> {
              assertThat(item.name()).isEqualTo("현재 세미나");
              assertThat(item.subAttendances())
                  .singleElement()
                  .satisfies(
                      a -> {
                        assertThat(a.attendedAt()).isEqualTo(today.minusHours(2));
                        assertThat(a.attendedAt()).isNotEqualTo(updatedAt);
                      });
            });
  }

  @Test
  @DisplayName("진행 전 세션과 이전 기수는 집계에서 제외하고 종료 세션만 최신순으로 반환한다")
  void separatesTotalsFromHistory() {
    var user = user();
    attendance(user, lecture("예정", 39, Part.SERVER, LectureStatus.BEFORE, TIME.plusDays(1)));
    attendance(user, lecture("최근", 39, Part.SERVER, LectureStatus.END, TIME));
    attendance(user, lecture("과거", 39, Part.SERVER, LectureStatus.END, TIME.minusDays(1)));
    attendance(user, lecture("이전 기수", 38, Part.WEB, LectureStatus.END, TIME.minusDays(2)));
    em.flush();
    em.clear();
    var summary = members.getMemberTotalAttendance(user.getId());
    assertThat(summary.member().generation()).isEqualTo(39);
    assertThat(summary.member().attendanceScore()).isEqualTo(2f);
    assertThat(summary.countByStatus(AttendanceStatus.ABSENT)).isEqualTo(2);
    assertThat(summary.attendances()).extracting(a -> a.lectureName()).containsExactly("최근", "과거");
  }

  @Test
  @DisplayName("종료한 세션의 출석 처리 결과가 상태와 회원 점수에 함께 반영된다")
  void persistsAttendanceAndScore() {
    var user = user();
    var now = LocalDateTime.now().minusMinutes(1);
    var lecture = lecture("세미나", 39, Part.SERVER, LectureStatus.END, now);
    var attendance = attendance(user, lecture);
    var first = SubLectureEntity.create(lecture, 1);
    first.updateCodeAndStartAt("1234", now);
    em.persist(first);
    var second = SubLectureEntity.create(lecture, 2);
    second.updateCodeAndStartAt("5678", now);
    em.persist(second);
    em.persist(SubAttendanceEntity.create(attendance, first));
    em.persist(SubAttendanceEntity.create(attendance, second));
    em.flush();
    em.clear();
    assertThat(attendanceService.attend(user.getId(), first.getId(), "1234"))
        .isEqualTo(first.getId());
    em.flush();
    em.clear();
    assertThat(em.find(AttendanceEntity.class, attendance.getId()).getStatus())
        .isEqualTo(AttendanceStatus.TARDY);
    assertThat(members.getMemberScore(user.getId())).isEqualTo(1.5f);
  }

  @Test
  @DisplayName("조회 구간에 시작 또는 종료가 포함된 일정만 시작 시각순으로 반환한다")
  void findsSchedulesByEndpoints() {
    schedule("구간 전체를 감싸는 일정", TIME.minusDays(1), TIME.plusDays(2));
    schedule("종료 경계", TIME.plusDays(1), TIME.plusDays(2));
    schedule("시작 경계", TIME.minusDays(1), TIME);
    schedule("구간 내부", TIME.plusHours(1), TIME.plusHours(2));
    em.flush();
    em.clear();
    assertThat(schedules.findBetween(TIME, TIME.plusDays(1)))
        .extracting(s -> s.title())
        .containsExactly("시작 경계", "구간 내부", "종료 경계");
  }

  private void schedule(String title, LocalDateTime start, LocalDateTime end) {
    em.getEntityManager()
        .createNativeQuery(
            "INSERT INTO schedules (start_date, end_date, attribute, title, created_at, updated_at) VALUES (:start, :end, 'EVENT', :title, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
        .setParameter("start", start)
        .setParameter("end", end)
        .setParameter("title", title)
        .executeUpdate();
  }
}
