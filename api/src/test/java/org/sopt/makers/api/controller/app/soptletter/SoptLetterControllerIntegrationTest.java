package org.sopt.makers.api.controller.app.soptletter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sopt.makers.api.support.SoptLetterIntegrationTestSupport;
import org.sopt.makers.domain.app.operationconfig.OperationConfigCategory;
import org.sopt.makers.domain.app.operationconfig.service.OperationConfigService;
import org.sopt.makers.domain.app.push.PushMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class SoptLetterControllerIntegrationTest extends SoptLetterIntegrationTestSupport {

  private static final String BASE = "/api/v2/sopt-letter";

  @Autowired OperationConfigService operationConfigService;

  @Test
  @DisplayName("주제 목록은 기본 주제를 먼저 주고, 기간 밖 주제를 빼고, 서브타이틀과 NEW, HOT, 안 읽은 수를 내려준다")
  void topicsResponse() throws Exception {
    long defaultTopic = topic("기본 주제", "부제", true, now.minusDays(1), now.plusDays(1), now);
    long normalTopic =
        topic("개별 주제", null, false, now.minusDays(1), now.plusDays(1), now.minusDays(8));
    topic("만료 주제", null, false, now.minusDays(10), now.minusDays(1), now.minusDays(10));
    letter(otherProfileId, defaultTopic, now.minusHours(1));
    letter(otherProfileId, defaultTopic, now.minusHours(1));

    mvc.perform(get(BASE + "/topics"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.topics.length()").value(2))
        .andExpect(jsonPath("$.data.topics[0].topicId").value(defaultTopic))
        .andExpect(jsonPath("$.data.topics[0].subtitle").value("부제"))
        .andExpect(jsonPath("$.data.topics[0].isDefault").value(true))
        .andExpect(jsonPath("$.data.topics[0].isNew").value(true))
        .andExpect(jsonPath("$.data.topics[0].isHot").value(true))
        .andExpect(jsonPath("$.data.topics[0].unreadCount").value(2))
        .andExpect(jsonPath("$.data.topics[1].topicId").value(normalTopic))
        .andExpect(jsonPath("$.data.topics[1].isNew").value(false))
        .andExpect(jsonPath("$.data.topics[1].isHot").value(false))
        .andExpect(jsonPath("$.data.topics[1].unreadCount").value(0));
  }

  @Test
  @DisplayName("메시지를 작성하면 작성한 메시지를 돌려준다")
  void writeMessage() throws Exception {
    long topic = topic("기본 주제", null, true, now.minusDays(1), now.plusDays(1), now);

    mvc.perform(
            post(BASE + "/topics/" + topic + "/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"반가워요\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content").value("반가워요"))
        .andExpect(jsonPath("$.data.mine").value(true));
  }

  @Test
  @DisplayName("기간 밖 주제에 메시지를 쓰면 400을 돌려준다")
  void rejectsWritingOutsidePeriod() throws Exception {
    long expired =
        topic("만료 주제", null, false, now.minusDays(10), now.minusDays(1), now.minusDays(10));

    mvc.perform(
            post(BASE + "/topics/" + expired + "/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"늦은 글\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("첫 페이지를 조회하면 비동기 리스너가 읽음 기록을 남기고 안 읽은 수가 0이 된다")
  void firstPageMarksTopicAsRead() throws Exception {
    assumeTrue(DB_URL != null, "읽음 기록 upsert는 PostgreSQL 문법이라 로컬 PostgreSQL에서만 돈다");
    long topic = topic("기본 주제", null, true, now.minusDays(1), now.plusDays(1), now);
    letter(otherProfileId, topic, now.minusHours(2));
    long latest = letter(otherProfileId, topic, now.minusHours(1));

    mvc.perform(get(BASE + "/topics/" + topic + "/messages").param("size", "1"))
        .andExpect(status().isOk());

    assertThat(waitForLastRead(topic)).isEqualTo(latest);
    mvc.perform(get(BASE + "/topics")).andExpect(jsonPath("$.data.topics[0].unreadCount").value(0));
  }

  @Test
  @DisplayName("좋아요를 누르면 커밋 뒤 현재 기수 활동 회원인 글쓴이에게 알림을 보낸다")
  void likeSendsAlarmToActiveAuthor() throws Exception {
    assumeTrue(DB_URL != null, "좋아요 저장은 PostgreSQL 문법이라 로컬 PostgreSQL에서만 돈다");
    operationConfigService.upsertValue(
        OperationConfigCategory.SOPT_LETTER, "likeNotificationTitle", "좋아요가 달렸어요", "제목");
    operationConfigService.upsertValue(
        OperationConfigCategory.SOPT_LETTER, "likeNotificationContent", "확인해 보세요", "내용");
    long author = user(CURRENT_GENERATION);
    long authorProfileId = profile(author, "익명의 고소한 붕어빵", false);
    long topic = topic("기본 주제", null, true, now.minusDays(1), now.plusDays(1), now);
    long letterId = letter(authorProfileId, topic, now.minusHours(1));

    mvc.perform(post(BASE + "/topics/" + topic + "/messages/" + letterId + "/likes"))
        .andExpect(status().isOk());

    ArgumentCaptor<PushMessage> captor = ArgumentCaptor.forClass(PushMessage.class);
    verify(pushSenderPort, timeout(5000)).send(captor.capture());
    PushMessage push = captor.getValue();
    assertThat(push.userIds()).containsExactly(author);
    assertThat(push.title()).isEqualTo("좋아요가 달렸어요");
    assertThat(push.deepLink())
        .isEqualTo(
            "soptletter?topicId="
                + topic
                + "&messageId="
                + letterId
                + "&pushType=soptletter_liked");
  }

  @Test
  @DisplayName("내 글에 좋아요를 누르면 403을 돌려준다")
  void cannotLikeOwnMessage() throws Exception {
    long myProfileId =
        jdbc.queryForObject("SELECT id FROM sopt_letter_profile WHERE user_id = ?", Long.class, ME);
    long topic = topic("기본 주제", null, true, now.minusDays(1), now.plusDays(1), now);
    long letterId = letter(myProfileId, topic, now.minusHours(1));

    mvc.perform(post(BASE + "/topics/" + topic + "/messages/" + letterId + "/likes"))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Flyway가 V4를 적용하고 성공으로 기록한다")
  void flywayAppliesV4() {
    assumeTrue(DB_URL != null, "Flyway는 로컬 PostgreSQL을 지정했을 때만 켠다");

    Boolean success =
        jdbc.queryForObject(
            "SELECT success FROM sopt_letter_test.flyway_schema_history WHERE version = '4'",
            Boolean.class);

    assertThat(success).isTrue();
  }

  private Long waitForLastRead(long topicId) throws InterruptedException {
    for (int i = 0; i < 50; i++) {
      List<Long> found =
          jdbc.queryForList(
              "SELECT last_read_letter_id FROM sopt_letter_topic_read"
                  + " WHERE user_id = ? AND topic_id = ?",
              Long.class,
              ME,
              topicId);
      if (!found.isEmpty()) {
        return found.getFirst();
      }
      Thread.sleep(100);
    }
    return null;
  }
}
