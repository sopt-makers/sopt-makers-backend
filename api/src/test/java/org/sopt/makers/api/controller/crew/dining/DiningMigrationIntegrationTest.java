package org.sopt.makers.api.controller.crew.dining;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityManager;
import java.time.*;
import java.util.*;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.*;
import org.sopt.makers.domain.auth.port.*;
import org.sopt.makers.domain.crew.dining.command.*;
import org.sopt.makers.domain.crew.dining.exception.*;
import org.sopt.makers.domain.crew.dining.model.*;
import org.sopt.makers.domain.crew.dining.model.enums.*;
import org.sopt.makers.domain.crew.dining.port.*;
import org.sopt.makers.domain.crew.dining.query.*;
import org.sopt.makers.domain.crew.dining.service.*;
import org.sopt.makers.domain.crew.meeting.*;
import org.sopt.makers.domain.crew.meeting.port.*;
import org.sopt.makers.storage.db.crew.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
    properties = {
      "crew.dining.default-image=https://example.test/default.png",
      "spring.datasource.url=jdbc:h2:mem:dining;MODE=PostgreSQL;NON_KEYWORDS=VALUE;DB_CLOSE_DELAY=-1"
    })
@Transactional
class DiningMigrationIntegrationTest {
  @MockitoBean OAuthAuthenticatorPort oAuthAuthenticatorPort;
  @MockitoBean TokenIssuerPort tokenIssuerPort;
  @MockitoBean SmsSenderPort smsSenderPort;
  @MockitoBean OkHttpClient gabiaOkHttpClient;
  @MockitoBean Clock clock;
  @MockitoBean DiningUserPort users;
  @MockitoBean DiningCsvPort csv;
  @MockitoBean DiningNotificationPort notifications;
  @MockitoBean MeetingActiveGenerationPort generation;
  @Autowired DiningV2Service service;
  @Autowired DiningReminderService reminders;
  @Autowired DiningRoundPort rounds;
  @Autowired DiningAttendancePort attendances;
  @Autowired DiningDeletionHistoryJpaRepository histories;
  @Autowired MeetingRepositoryPort ordinaryMeetings;
  @Autowired EntityManager em;
  @Autowired JdbcTemplate jdbc;
  @Autowired org.springframework.transaction.PlatformTransactionManager transactionManager;
  @Autowired DiningMeetingPort diningMeetings;
  long leader;
  long member;
  long stranger;

  // 로컬 PostgreSQL을 지정하면 같은 계약을 실제 SQL/FK/JSONB 환경에서 검증한다.
  @DynamicPropertySource
  static void database(DynamicPropertyRegistry properties) {
    String url = System.getenv("DINING_TEST_DB_URL");
    if (url != null) {
      properties.add("spring.datasource.url", () -> url);
      properties.add("spring.datasource.username", () -> System.getenv("DINING_TEST_DB_USER"));
      properties.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
      properties.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
      properties.add("spring.flyway.enabled", () -> "true");
      properties.add("spring.flyway.schemas", () -> "dining_test");
      properties.add("spring.flyway.default-schema", () -> "dining_test");
    }
  }

  @BeforeEach
  void setup() {
    when(clock.withZone(any())).thenReturn(clock);
    when(clock.instant()).thenReturn(Instant.parse("2026-11-13T03:00:00Z"));
    when(clock.getZone()).thenReturn(ZoneId.of("Asia/Seoul"));
    when(generation.getActiveGeneration()).thenReturn(39);
    leader = user("개설자");
    member = user("참석자");
    stranger = user("다른 사용자");
    when(users.findByIdOrThrow(anyLong())).thenCallRealMethod();
    when(users.findAllByIdInOrThrow(anyList())).thenCallRealMethod();
    when(users.findById(anyLong())).thenAnswer(i -> Optional.of(snapshot(i.getArgument(0))));
    when(users.findAllById(anyList()))
        .thenAnswer(
            i -> ((List<Long>) i.getArgument(0)).stream().distinct().map(this::snapshot).toList());
    when(notifications.send(anyList(), anyString(), anyString(), anyString())).thenReturn(true);
  }

  long user(String name) {
    String key = UUID.randomUUID().toString();
    jdbc.update(
        "INSERT INTO users(name,phone,auth_platform_id,auth_platform_type,is_first_login,is_phone_blind,created_at,updated_at) VALUES(?,?,?,'GOOGLE',false,false,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)",
        name,
        "01012345678",
        key);
    return jdbc.queryForObject("SELECT id FROM users WHERE auth_platform_id=?", Long.class, key);
  }

  DiningUser snapshot(Long id) {
    return new DiningUser(id, "사용자" + id, "https://example.test/profile.png", "01012345678", 39);
  }

  CreateAndUpdateDiningCommand command(String title, String date) {
    return new CreateAndUpdateDiningCommand(
        title,
        "장소",
        "https://naver.me/test",
        null,
        date,
        "18:00",
        new MeetingJoinablePart[] {MeetingJoinablePart.SERVER},
        List.of());
  }

  long create() {
    return service.createDining(command("서버 회식", "2026.11.14"), leader).meetingId();
  }

  long first(long id) {
    return rounds.findAllByMeetingIdOrderByIdAsc(id).getFirst().getId();
  }

  long next(long id) {
    return service
        .createRound(id, new CreateAndUpdateRoundCommand("2차", null, null, "21:00"), leader)
        .roundId();
  }

  void reload() {
    em.flush();
    em.clear();
  }

  @Test
  void 생성_수정_종료가_별도_도메인_모델에서도_저장된다() {
    long id = create(), round = first(id);
    reload();
    assertThat(service.getAttendance(id, round, leader).isAttending()).isTrue();
    assertThat(service.getDiningDetail(id, leader).imageUrl())
        .isEqualTo("https://example.test/default.png");
    service.updateDining(id, command("변경 회식", "2026.11.15"), leader);
    reload();
    var detail = service.getDiningDetail(id, leader);
    assertThat(detail.title()).isEqualTo("변경 회식");
    assertThat(detail.isDateModified()).isTrue();
    long second = next(id);
    service.updateRound(
        id, second, new CreateAndUpdateRoundCommand("변경 장소", null, null, "22:00"), leader);
    reload();
    assertThat(rounds.findByIdOrThrow(second).getPlace()).isEqualTo("변경 장소");
    service.endDining(id, leader);
    reload();
    assertThat(service.getDiningDetail(id, leader).isEnded()).isTrue();
    assertThat(rounds.findAllByMeetingIdOrderByIdAsc(id)).allMatch(DiningRound::isClosed);
    assertThatThrownBy(() -> next(id)).isInstanceOf(DiningException.class);
  }

  @Test
  void 공동결제자는_승인된_회차만_관리하고_해제후에는_권한이_없다() {
    long id = create(), round = first(id), second = next(id);
    service.applyAttendance(id, round, member);
    service.requestPayer(id, round, member);
    reload();
    assertThat(service.getPayerRequests(id, leader).requests()).hasSize(1);
    assertThatThrownBy(
            () -> service.getAttendees(id, round, new GetAttendeesQuery(1, 10, "asc"), member))
        .isInstanceOf(DiningException.class);
    service.approvePayer(id, round, member, leader);
    reload();
    assertThat(service.getApprovedPayers(id, leader).payers()).hasSize(1);
    service.addAttendee(id, round, stranger, member);
    assertThatThrownBy(() -> service.addAttendee(id, second, stranger, member))
        .isInstanceOf(DiningException.class);
    service.revokePayer(id, round, member, leader);
    reload();
    assertThat(service.getAttendance(id, round, member).payerStatus()).isNull();
    assertThatThrownBy(() -> service.getAttendeesCsv(id, round, member))
        .isInstanceOf(DiningException.class);
  }

  @Test
  void 결제요청_거절은_참석을_유지하고_재요청을_허용한다() {
    long id = create(), round = first(id);
    service.applyAttendance(id, round, member);
    service.requestPayer(id, round, member);
    service.rejectPayer(id, round, member, leader);
    reload();
    assertThat(service.getAttendance(id, round, member).isAttending()).isTrue();
    assertThat(service.getAttendance(id, round, member).payerStatus()).isNull();
    service.requestPayer(id, round, member);
    reload();
    assertThat(service.getAttendance(id, round, member).payerStatus())
        .isEqualTo(DiningPayerStatus.PENDING);
  }

  @Test
  void 삭제_이력은_마지막_결제상태와_스냅샷을_보존한다() {
    long id = create(), round = first(id);
    service.applyAttendance(id, round, member);
    service.requestPayer(id, round, member);
    service.approvePayer(id, round, member, leader);
    service.cancelAttendance(id, round, member);
    reload();
    var history =
        histories.findAll().stream()
            .filter(h -> h.getMeetingId().equals(id))
            .findFirst()
            .orElseThrow();
    assertThat(((Map<?, ?>) history.getSnapshot().get("data")).get("createdTimestamp")).isNotNull();
    assertThat(history.getTargetType()).isEqualTo(DiningDeletionTargetType.ATTENDANCE);
    assertThat(((Map<?, ?>) history.getSnapshot().get("data")).get("payerStatus"))
        .isEqualTo("APPROVED");
    service.deleteDining(id, leader);
    reload();
    assertThat(rounds.findAllByMeetingIdOrderByIdAsc(id)).isEmpty();
    assertThat(histories.findAll().stream().filter(h -> h.getMeetingId().equals(id))).hasSize(2);
  }

  @Test
  void 첫회차와_마감된회차는_삭제불가하고_중간삭제후_번호가_당겨진다() {
    long id = create(), one = first(id), two = next(id), three = next(id);
    assertThatThrownBy(() -> service.deleteRound(id, one, leader))
        .isInstanceOf(DiningException.class);
    service.deleteRound(id, two, leader);
    reload();
    assertThat(service.getDiningDetail(id, leader).rounds().get(1).roundId()).isEqualTo(three);
    assertThat(service.getDiningDetail(id, leader).rounds().get(1).roundNumber()).isEqualTo(2);
    service.closeRound(id, three, leader);
    reload();
    assertThatThrownBy(() -> service.deleteRound(id, three, leader))
        .isInstanceOf(DiningException.class);
    assertThatThrownBy(() -> service.deleteDining(id, leader)).isInstanceOf(DiningException.class);
  }

  @Test
  void 마감후_자율참석은_불가하지만_관리자는_참석자를_수정한다() {
    long id = create(), round = first(id);
    service.closeRound(id, round, leader);
    reload();
    assertThatThrownBy(() -> service.applyAttendance(id, round, member))
        .isInstanceOf(DiningException.class);
    service.addAttendee(id, round, member, leader);
    reload();
    assertThatThrownBy(() -> service.cancelAttendance(id, round, member))
        .isInstanceOf(DiningException.class);
    service.deleteAttendee(id, round, member, leader);
    reload();
    assertThat(service.getAttendance(id, round, member).isAttending()).isFalse();
  }

  @Test
  void 정렬_페이지번호_CSV_참석현황_순서를_유지한다() {
    long id = create(), round = first(id);
    service.applyAttendance(id, round, member);
    service.applyAttendance(id, round, stranger);
    reload();
    var asc = service.getAttendees(id, round, new GetAttendeesQuery(1, 10, "asc"), leader);
    var desc = service.getAttendees(id, round, new GetAttendeesQuery(1, 10, "desc"), leader);
    assertThat(asc.attendees()).extracting(a -> a.attendanceNumber()).containsExactly(1L, 2L, 3L);
    assertThat(desc.attendees()).extracting(a -> a.attendanceNumber()).containsExactly(3L, 2L, 1L);
    assertThat(service.getAttendanceAttendees(id, round, member).attendees())
        .extracting(a -> a.id())
        .containsExactly(leader, member, stranger);
    when(csv.upload(anyList())).thenReturn("https://example.test/export.csv");
    service.getAttendeesCsv(id, round, leader);
    verify(csv)
        .upload(
            argThat(
                list ->
                    list.stream()
                        .map(DiningUser::getId)
                        .toList()
                        .equals(List.of(leader, member, stranger))));
  }

  @Test
  void 전체_내회식_파트_상태필터와_모임목록_제외를_검증한다() {
    long id = create();
    reload();
    assertThat(
            service
                .getDinings(
                    new GetDiningsQuery(1, 12, "ALL", null, MeetingJoinablePart.SERVER, "서버"),
                    member)
                .dinings())
        .extracting(d -> d.meetingId())
        .contains(id);
    assertThat(
            service
                .getDinings(new GetDiningsQuery(1, 12, "MY", null, null, null), member)
                .dinings())
        .isEmpty();
    service.applyAttendance(id, first(id), member);
    reload();
    assertThat(
            service
                .getDinings(new GetDiningsQuery(1, 12, "MY", null, null, null), member)
                .dinings())
        .hasSize(1);
    service.endDining(id, leader);
    reload();
    assertThat(
            service
                .getDinings(new GetDiningsQuery(1, 12, "ALL", "IN_PROGRESS", null, null), member)
                .dinings())
        .isEmpty();
    assertThat(
            service
                .getDinings(new GetDiningsQuery(1, 12, "ALL", "ENDED", null, null), member)
                .dinings())
        .hasSize(1);
    assertThat(ordinaryMeetings.findById(id)).isEmpty();
    assertThat(
            ordinaryMeetings
                .findAll(new org.sopt.makers.core.pagination.PageQuery(1, 100))
                .content())
        .noneMatch(m -> m.id().equals(id));
    assertThat(
            ordinaryMeetings
                .findAllByMemberUserId(
                    leader, new org.sopt.makers.core.pagination.PageQuery(1, 100))
                .content())
        .isEmpty();
    assertThat(ordinaryMeetings.findFirstIdByTitle("서버 회식")).isEmpty();
  }

  @Test
  void 자동마감과_알림대상은_변경된회식일자를_기준으로_조회한다() {
    long tomorrow = create();
    long today = service.createDining(command("오늘 회식", "2026.11.13"), leader).meetingId();
    reload();
    assertThat(reminders.findReminderTargets()).extracting(t -> t.meetingId()).contains(tomorrow);
    assertThat(reminders.findCloseGuideTargets()).extracting(t -> t.meetingId()).contains(today);
    service.updateDining(today, command("지난 회식", "2026.11.12"), leader);
    reload();
    assertThat(service.closeExpiredRounds()).isEqualTo(1);
    reload();
    assertThat(rounds.findByIdOrThrow(first(today)).isClosed()).isTrue();
    assertThat(service.closeExpiredRounds()).isZero();
  }

  @AfterEach
  void cleanCommittedFixtures() {
    if (org.springframework.test.context.transaction.TestTransaction.isActive()) return;
    // 동시성 테스트만 별도 트랜잭션으로 커밋하므로 해당 테스트가 생성한 데이터만 정리한다.
    var ids =
        jdbc.queryForList(
            "SELECT meeting_id FROM meeting_member WHERE user_id=? AND role='LEADER'",
            Long.class,
            leader);
    for (Long id : ids) {
      jdbc.update("DELETE FROM meeting_member WHERE meeting_id=?", id);
      jdbc.update("DELETE FROM meeting WHERE id=?", id);
    }
    jdbc.update(
        "DELETE FROM dining_deletion_history WHERE deleted_by_user_id IN (?,?,?)",
        leader,
        member,
        stranger);
    jdbc.update("DELETE FROM users WHERE id IN (?,?,?)", leader, member, stranger);
  }

  @Test
  @org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable(
      named = "DINING_TEST_DB_URL",
      matches = "jdbc:postgresql:.*")
  @Transactional(propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED)
  void 동시에_참석해도_한번만_등록된다() throws Exception {
    long id = create(), round = first(id);
    var start = new java.util.concurrent.CountDownLatch(1);
    try (var pool = java.util.concurrent.Executors.newFixedThreadPool(2)) {
      java.util.concurrent.Callable<Boolean> apply =
          () -> {
            start.await();
            try {
              service.applyAttendance(id, round, member);
              return true;
            } catch (DiningException e) {
              return false;
            }
          };
      var a = pool.submit(apply);
      var b = pool.submit(apply);
      start.countDown();
      assertThat(
              List.of(
                  a.get(5, java.util.concurrent.TimeUnit.SECONDS),
                  b.get(5, java.util.concurrent.TimeUnit.SECONDS)))
          .containsExactlyInAnyOrder(true, false);
      assertThat(service.getAttendance(id, round, member).attendeeCount()).isEqualTo(2);
    }
  }

  @Test
  @org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable(
      named = "DINING_TEST_DB_URL",
      matches = "jdbc:postgresql:.*")
  @Transactional(propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED)
  void 삭제중_참석은_삭제이력을_누락시키지_않고_실패한다() throws Exception {
    long id = create(), round = first(id);
    var locked = new java.util.concurrent.CountDownLatch(1);
    var release = new java.util.concurrent.CountDownLatch(1);
    var tx = new org.springframework.transaction.support.TransactionTemplate(transactionManager);
    try (var pool = java.util.concurrent.Executors.newFixedThreadPool(2)) {
      var deleting =
          pool.submit(
              () ->
                  tx.executeWithoutResult(
                      status -> {
                        diningMeetings.findDiningByIdForUpdateOrThrow(id);
                        locked.countDown();
                        try {
                          if (!release.await(5, java.util.concurrent.TimeUnit.SECONDS))
                            throw new IllegalStateException("잠금 대기 시간 초과");
                        } catch (InterruptedException e) {
                          Thread.currentThread().interrupt();
                          throw new RuntimeException(e);
                        }
                        service.deleteDining(id, leader);
                      }));
      assertThat(locked.await(5, java.util.concurrent.TimeUnit.SECONDS)).isTrue();
      var applying = pool.submit(() -> service.applyAttendance(id, round, member));
      try {
        assertThatThrownBy(() -> applying.get(150, java.util.concurrent.TimeUnit.MILLISECONDS))
            .isInstanceOf(java.util.concurrent.TimeoutException.class);
      } finally {
        release.countDown();
      }
      deleting.get(5, java.util.concurrent.TimeUnit.SECONDS);
      assertThatThrownBy(() -> applying.get(5, java.util.concurrent.TimeUnit.SECONDS))
          .isInstanceOf(java.util.concurrent.ExecutionException.class)
          .hasCauseInstanceOf(DiningException.class);
      assertThat(histories.findAll().stream().filter(h -> h.getMeetingId().equals(id))).hasSize(1);
      assertThat(attendances.findAllByRoundId(round)).isEmpty();
    }
  }

  @Test
  @org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable(
      named = "DINING_TEST_DB_URL",
      matches = "jdbc:postgresql:.*")
  @Transactional(propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED)
  void 회차알림은_커밋후_발송하며_롤백과_회식개설은_발송하지_않는다() {
    long id = create();
    verifyNoInteractions(notifications);
    service.applyAttendance(id, first(id), member);
    var tx = new org.springframework.transaction.support.TransactionTemplate(transactionManager);
    tx.executeWithoutResult(
        status -> {
          next(id);
          status.setRollbackOnly();
        });
    verifyNoInteractions(notifications);
    next(id);
    verify(notifications, timeout(3000))
        .send(
            eq(List.of(Long.toString(member))),
            eq("새로운 회차 추가"),
            anyString(),
            eq("/settlement/detail?id=" + id));
  }
}
