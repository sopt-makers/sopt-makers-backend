package org.sopt.makers.api.controller.app.soptamp;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.sopt.makers.api.controller.app.AppChannelMockMvc;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.service.SoptampUserService;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

class SoptampUserControllerTest {

  private static final Long USER_ID = 1L;

  private final SoptampUserService soptampUserService = mock(SoptampUserService.class);

  private final MockMvc mockMvc =
      AppChannelMockMvc.of(new SoptampUserController(soptampUserService), USER_ID);

  @Test
  void 솝탬프_정보_조회_응답_모양() throws Exception {
    given(soptampUserService.getSoptampUser(USER_ID))
        .willReturn(
            new SoptampUser(10L, USER_ID, "1등이 되고 말거야!", 15L, "김앱짱", 38L, SoptampPart.PLAN));

    mockMvc
        .perform(get("/api/v2/user/soptamp"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "솝탬프 정보 조회에 성공했습니다.",
                      "data": {
                        "nickname": "김앱짱",
                        "points": 15,
                        "profileMessage": "1등이 되고 말거야!"
                      }
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 솝탬프_유저가_없으면_404() throws Exception {
    given(soptampUserService.getSoptampUser(USER_ID))
        .willThrow(new SoptampException(SoptampFailure.NOT_FOUND_SOPTAMP_USER));

    mockMvc
        .perform(get("/api/v2/user/soptamp"))
        .andExpect(status().isNotFound())
        .andExpect(
            content()
                .json(
                    """
                    {"success": false, "message": "존재하지 않는 유저입니다.", "data": null}
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 한마디_변경_응답_모양() throws Exception {
    given(soptampUserService.editProfileMessage(USER_ID, "1등이 되고 말거야!"))
        .willReturn(
            new SoptampUser(10L, USER_ID, "1등이 되고 말거야!", 15L, "김앱짱", 38L, SoptampPart.PLAN));

    mockMvc
        .perform(
            patch("/api/v2/user/profile-message")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"profileMessage\": \"1등이 되고 말거야!\"}"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "한마디 변경에 성공했습니다.",
                      "data": {"profileMessage": "1등이 되고 말거야!"}
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 한마디가_null_이면_400_이고_구서버_메시지를_그대로_담는다() throws Exception {
    mockMvc
        .perform(
            patch("/api/v2/user/profile-message")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": false,
                      "message": "유효하지 않은 입력 값입니다",
                      "data": {"valid_profileMessage": "profileMessage may not be null"}
                    }
                    """,
                    JsonCompareMode.STRICT));
  }
}
