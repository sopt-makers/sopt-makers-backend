package org.sopt.makers.api.controller.app.soptamp;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.controller.app.AppChannelMockMvc;
import org.sopt.makers.domain.app.soptamp.clap.ClapUserProfile;
import org.sopt.makers.domain.app.soptamp.clap.service.ClapService;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.facade.SoptampFacade;
import org.sopt.makers.domain.app.soptamp.facade.SoptampFacade.ClapResult;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

class SoptampClapControllerTest {

  private static final Long USER_ID = 1L;

  private final SoptampFacade soptampFacade = mock(SoptampFacade.class);
  private final ClapService clapService = mock(ClapService.class);

  private final MockMvc mockMvc =
      AppChannelMockMvc.of(new SoptampClapController(soptampFacade, clapService), USER_ID);

  @Test
  void 박수치기_응답_모양() throws Exception {
    given(soptampFacade.addClap(USER_ID, 123L, 7)).willReturn(new ClapResult(5, 203));

    mockMvc
        .perform(
            post("/api/v2/stamp/123/clap")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"clapCount\": 7}"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "스탬프 박수치기에 성공했습니다.",
                      "data": {"stampId": 123, "appliedCount": 5, "totalClapCount": 203}
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 박수_수가_양수가_아니면_400_이고_구서버_메시지를_그대로_담는다() throws Exception {
    mockMvc
        .perform(
            post("/api/v2/stamp/123/clap")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"clapCount\": 0}"))
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": false,
                      "message": "유효하지 않은 입력 값입니다",
                      "data": {"valid_clapCount": "clapCount must be > 0"}
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 자기_스탬프에_박수치면_403() throws Exception {
    given(soptampFacade.addClap(USER_ID, 123L, 7))
        .willThrow(new SoptampException(SoptampFailure.FORBIDDEN_SELF_CLAP));

    mockMvc
        .perform(
            post("/api/v2/stamp/123/clap")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"clapCount\": 7}"))
        .andExpect(status().isForbidden())
        .andExpect(
            content()
                .json(
                    """
                    {"success": false, "message": "타인의 스탬프에만 박수 칠 수 있습니다.", "data": null}
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 박수_친_유저_목록_응답_모양() throws Exception {
    given(clapService.getClapsOfMyStamp(USER_ID, 123L, PageRequest.of(0, 20)))
        .willReturn(
            new PageImpl<>(
                List.of(
                    new ClapUserProfile(
                        "서버이지훈", "https://cdn.sopt.org/profile/1024.jpg", "뒹굴뒹굴 ~,~", 12)),
                PageRequest.of(0, 20),
                95));

    mockMvc
        .perform(get("/api/v2/stamp/123/clappers").param("page", "0").param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "박수 친 유저 목록 조회에 성공했습니다.",
                      "data": {
                        "users": [
                          {
                            "nickname": "서버이지훈",
                            "profileImageUrl": "https://cdn.sopt.org/profile/1024.jpg",
                            "profileMessage": "뒹굴뒹굴 ~,~",
                            "clapCount": 12
                          }
                        ],
                        "totalPageSize": 5,
                        "pageSize": 20,
                        "pageNum": 0
                      }
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 범위를_벗어난_page_와_size_는_기본값으로_보정한다() throws Exception {
    given(clapService.getClapsOfMyStamp(USER_ID, 123L, PageRequest.of(0, 25)))
        .willReturn(new PageImpl<>(List.of(), PageRequest.of(0, 25), 0));

    mockMvc
        .perform(get("/api/v2/stamp/123/clappers").param("page", "-3").param("size", "0"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "박수 친 유저 목록 조회에 성공했습니다.",
                      "data": {"users": [], "totalPageSize": 0, "pageSize": 25, "pageNum": 0}
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 남의_스탬프_박수_목록은_403() throws Exception {
    given(clapService.getClapsOfMyStamp(USER_ID, 123L, PageRequest.of(0, 25)))
        .willThrow(new SoptampException(SoptampFailure.FORBIDDEN_CLAP_LIST));

    mockMvc
        .perform(get("/api/v2/stamp/123/clappers"))
        .andExpect(status().isForbidden())
        .andExpect(
            content()
                .json(
                    """
                    {"success": false, "message": "내 미션에서만 박수 목록을 조회할 수 있습니다.", "data": null}
                    """,
                    JsonCompareMode.STRICT));
  }
}
