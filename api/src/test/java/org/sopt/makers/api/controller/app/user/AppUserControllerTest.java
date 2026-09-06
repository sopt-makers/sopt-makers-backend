package org.sopt.makers.api.controller.app.user;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.controller.app.AppChannelMockMvc;
import org.sopt.makers.domain.app.home.ActivityStatus;
import org.sopt.makers.domain.app.home.MainView;
import org.sopt.makers.domain.app.home.UserActiveInfo;
import org.sopt.makers.domain.app.home.facade.HomeFacade;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

class AppUserControllerTest {

  private static final Long USER_ID = 1L;

  private final HomeFacade homeFacade = mock(HomeFacade.class);
  private final MockMvc mockMvc = AppChannelMockMvc.of(new AppUserController(homeFacade), USER_ID);
  private final MockMvc anonymousMockMvc =
      AppChannelMockMvc.ofAnonymous(new AppUserController(homeFacade));

  @Test
  void 토큰_없는_메인_뷰는_UNAUTHENTICATED_기본값() throws Exception {
    given(homeFacade.getMainViewInfo(null)).willReturn(MainView.unauthenticated());

    anonymousMockMvc
        .perform(get("/api/v2/user/main"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "메인 뷰 조회에 성공했습니다.",
                      "data": {
                        "user": {"status": "UNAUTHENTICATED", "name": "", "profileImage": "", "part": "", "generationList": []},
                        "operation": {"attendanceScore": 0.0, "announcement": ""},
                        "isAllConfirm": false
                      }
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 메인_뷰_성공_응답_모양() throws Exception {
    given(homeFacade.getMainViewInfo(USER_ID))
        .willReturn(
            new MainView(
                ActivityStatus.ACTIVE,
                "홍길동",
                "https://img/p.png",
                "WEB/SERVER",
                List.of(38L, 34L),
                true));

    mockMvc
        .perform(get("/api/v2/user/main"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "메인 뷰 조회에 성공했습니다.",
                      "data": {
                        "user": {"status": "ACTIVE", "name": "홍길동", "profileImage": "https://img/p.png", "part": "WEB/SERVER", "generationList": [38, 34]},
                        "operation": {"attendanceScore": 0.0, "announcement": ""},
                        "isAllConfirm": true
                      }
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 기수_정보_성공_응답_모양() throws Exception {
    given(homeFacade.getUserActiveInfo(USER_ID))
        .willReturn(new UserActiveInfo(38L, ActivityStatus.INACTIVE));

    mockMvc
        .perform(get("/api/v2/user/generation"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "기수 정보 조회에 성공했습니다.",
                      "data": {"currentGeneration": 38, "status": "INACTIVE"}
                    }
                    """,
                    JsonCompareMode.STRICT));
  }
}
