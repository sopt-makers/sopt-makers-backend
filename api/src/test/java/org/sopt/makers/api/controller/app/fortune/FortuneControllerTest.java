package org.sopt.makers.api.controller.app.fortune;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.controller.app.AppChannelMockMvc;
import org.sopt.makers.domain.app.fortune.FortuneCard;
import org.sopt.makers.domain.app.fortune.exception.FortuneException;
import org.sopt.makers.domain.app.fortune.exception.FortuneFailure;
import org.sopt.makers.domain.app.fortune.facade.FortuneFacade;
import org.sopt.makers.domain.app.fortune.facade.FortuneFacade.TodayFortuneWord;
import org.sopt.makers.domain.app.fortune.service.FortuneService;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 구 앱 서버(sopt-backend) fortune API 응답 계약 characterization 테스트. 성공/에러 JSON 모양을 문자 그대로 고정한다. 앱 클라이언트가
 * BaseResponse로 전환되기 전까지 이 모양이 깨지면 안 된다.
 */
class FortuneControllerTest {

  private static final Long USER_ID = 1L;

  private final FortuneFacade fortuneFacade = mock(FortuneFacade.class);
  private final FortuneService fortuneService = mock(FortuneService.class);

  private final MockMvc mockMvc =
      AppChannelMockMvc.of(new FortuneController(fortuneFacade, fortuneService), USER_ID);

  @Test
  void 오늘의_운세_단어_성공_응답_모양() throws Exception {
    given(fortuneFacade.getTodayFortuneWord(USER_ID, LocalDate.of(2026, 7, 15)))
        .willReturn(new TodayFortuneWord("차은우", "오늘은 코드가 잘 풀리는 날"));

    mockMvc
        .perform(get("/api/v2/fortune/word").param("todayDate", "2026-07-15"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {"userName":"차은우","title":"오늘은 코드가 잘 풀리는 날"}
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 오늘의_운세_카드_성공_응답_모양() throws Exception {
    given(fortuneService.getTodayFortuneCard(USER_ID))
        .willReturn(
            new FortuneCard(10L, "맑음 카드", "좋은 일이 생겨요", "https://img.example/card.png", "#FFEE00"));

    mockMvc
        .perform(get("/api/v2/fortune/card/today"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "name": "맑음 카드",
                      "description": "좋은 일이 생겨요",
                      "imageUrl": "https://img.example/card.png",
                      "imageColorCode": "#FFEE00"
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 운세_없음_404_에러_포맷_보존() throws Exception {
    given(fortuneService.getTodayFortuneCard(USER_ID))
        .willThrow(new FortuneException(FortuneFailure.NOT_FOUND_FORTUNE_FROM_USER));

    mockMvc
        .perform(get("/api/v2/fortune/card/today"))
        .andExpect(status().isNotFound())
        .andExpect(
            content()
                .json(
                    """
                    {"message":"유저에게 할당된 오늘의 운세가 없습니다.","status":"NOT_FOUND","errors":[]}
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 필수_파라미터_누락_INVALID_PARAMETER_포맷_응답() throws Exception {
    mockMvc
        .perform(get("/api/v2/fortune/word"))
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "message": "잘못된 파라미터 입니다.",
                      "status": "BAD_REQUEST",
                      "errors": [
                        {"field": "todayDate", "value": "", "reason": "required"}
                      ]
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 파라미터_타입_불일치_구_FailureResponse_포맷_보존() throws Exception {
    mockMvc
        .perform(get("/api/v2/fortune/word").param("todayDate", "2026-13-99"))
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "message": "잘못된 파라미터 입니다.",
                      "status": "BAD_REQUEST",
                      "errors": [
                        {"field": "todayDate", "value": "2026-13-99", "reason": "typeMismatch"}
                      ]
                    }
                    """,
                    JsonCompareMode.STRICT));
  }
}
