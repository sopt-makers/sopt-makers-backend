package org.sopt.makers.api.controller.app.home;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.support.SoptLetterIntegrationTestSupport;

class HomeControllerIntegrationTest extends SoptLetterIntegrationTestSupport {

  private static final String APP_SERVICE = "/api/v2/home/app-service";

  @BeforeEach
  void setUpHome() {
    long member = user(CURRENT_GENERATION);
    profile(member, "익명의 따뜻한 국밥", false);
    loginAs(member);
    jdbc.update(
        "INSERT INTO app_service"
            + " (service_name, active_user, inactive_user, created_at, updated_at)"
            + " VALUES ('SOPT_LETTER', true, true, ?, ?)",
        now,
        now);
  }

  @Test
  @DisplayName("홈 앱 서비스의 솝레터 뱃지는 안 읽은 수 합계를 보여 준다")
  void soptLetterBadge() throws Exception {
    long topic = topic("기본 주제", null, true, now.minusDays(1), now.plusDays(1), now);
    letter(otherProfileId, topic, now.minusHours(1));
    letter(otherProfileId, topic, now.minusHours(1));
    letter(otherProfileId, topic, now.minusHours(1));

    mvc.perform(get(APP_SERVICE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.appServices[0].displayAlarmBadge").value(true))
        .andExpect(jsonPath("$.data.appServices[0].alarmBadge").value("3"));
  }
}
