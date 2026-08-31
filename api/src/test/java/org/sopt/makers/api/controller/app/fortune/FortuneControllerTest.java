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
 * fortune API 응답 계약 테스트. 통합 레포 표준 BaseResponse({success, message, data}) 모양을 문자 그대로 고정한다. 앱 채널 다른
 * 도메인(poke 등)도 같은 봉투를 따르므로 이 패턴을 재사용한다.
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
                    {
                      "success": true,
                      "message": "오늘의 솝마디 조회에 성공했습니다.",
                      "data": {"userName": "차은우", "title": "오늘은 코드가 잘 풀리는 날"}
                    }
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
                      "success": true,
                      "message": "오늘의 운세카드 조회에 성공했습니다.",
                      "data": {
                        "name": "맑음 카드",
                        "description": "좋은 일이 생겨요",
                        "imageUrl": "https://img.example/card.png",
                        "imageColorCode": "#FFEE00"
                      }
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 운세_없음_404_BaseResponse_포맷() throws Exception {
    given(fortuneService.getTodayFortuneCard(USER_ID))
        .willThrow(new FortuneException(FortuneFailure.NOT_FOUND_FORTUNE_FROM_USER));

    mockMvc
        .perform(get("/api/v2/fortune/card/today"))
        .andExpect(status().isNotFound())
        .andExpect(
            content()
                .json(
                    """
                    {"success": false, "message": "유저에게 할당된 오늘의 운세가 없습니다.", "data": null}
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 필수_파라미터_누락_400_BaseResponse_포맷() throws Exception {
    mockMvc
        .perform(get("/api/v2/fortune/word"))
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .json(
                    """
                    {"success": false, "message": "필수 요청 파라미터가 누락되었습니다", "data": null}
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 파라미터_타입_불일치_400_BaseResponse_포맷() throws Exception {
    mockMvc
        .perform(get("/api/v2/fortune/word").param("todayDate", "2026-13-99"))
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .json(
                    """
                    {"success": false, "message": "입력한 값의 타입이 잘못되었습니다", "data": null}
                    """,
                    JsonCompareMode.STRICT));
  }
}
