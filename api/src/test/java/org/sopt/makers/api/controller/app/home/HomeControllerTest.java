package org.sopt.makers.api.controller.app.home;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.controller.app.AppChannelMockMvc;
import org.sopt.makers.domain.app.home.AppServiceEntryStatus;
import org.sopt.makers.domain.app.home.FloatingButton;
import org.sopt.makers.domain.app.home.HomeAppServices;
import org.sopt.makers.domain.app.home.exception.HomeException;
import org.sopt.makers.domain.app.home.exception.HomeFailure;
import org.sopt.makers.domain.app.home.facade.HomeFacade;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPost;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPost;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

class HomeControllerTest {

  private static final Long USER_ID = 1L;

  private final HomeFacade homeFacade = mock(HomeFacade.class);
  private final MockMvc mockMvc = AppChannelMockMvc.of(new HomeController(homeFacade), USER_ID);
  private final MockMvc anonymousMockMvc =
      AppChannelMockMvc.ofAnonymous(new HomeController(homeFacade));

  @Test
  void 홈_설명_성공_응답_모양() throws Exception {
    given(homeFacade.getHomeMainDescription(USER_ID)).willReturn("<b>홍길동</b>님은<br>SOPT와 31개월째");

    mockMvc
        .perform(get("/api/v2/home/description"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "홈 설명 조회에 성공했습니다.",
                      "data": {"activityDescription": "<b>홍길동</b>님은<br>SOPT와 31개월째"}
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 홈_앱_서비스_응답_키() throws Exception {
    given(homeFacade.getHomeAppServices(USER_ID))
        .willReturn(
            new HomeAppServices(
                false,
                List.of(
                    new AppServiceEntryStatus(
                        "솝레터", true, "N", "https://img/l.png", "sopt://letter"))));

    mockMvc
        .perform(get("/api/v2/home/app-service"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "홈 앱 서비스 조회에 성공했습니다.",
                      "data": {
                        "isAppjamMode": false,
                        "appServices": [{
                          "serviceName": "솝레터", "displayAlarmBadge": true, "alarmBadge": "N",
                          "iconUrl": "https://img/l.png", "deepLink": "sopt://letter"
                        }]
                      }
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 토큰_없는_탭_앱_서비스_호출은_200() throws Exception {
    given(homeFacade.checkTabAppServiceEntryStatus(null))
        .willReturn(List.of(new AppServiceEntryStatus("콕찌르기", false, "", null, "sopt://poke")));

    anonymousMockMvc
        .perform(get("/api/v2/home/tab-app-service"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "탭 앱 서비스 조회에 성공했습니다.",
                      "data": [{
                        "serviceName": "콕찌르기", "displayAlarmBadge": false, "alarmBadge": "",
                        "iconUrl": null, "deepLink": "sopt://poke"
                      }]
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 최신_게시글_응답_키() throws Exception {
    given(homeFacade.getPlaygroundRecentPosts())
        .willReturn(
            List.of(
                new PlaygroundRecentPost(
                    11L,
                    7L,
                    "https://img/p.png",
                    "홍길동",
                    "38기 서버",
                    "자유",
                    "제목",
                    "내용",
                    "https://pg/11",
                    "2026-09-01 10:00:00.000000",
                    false)));

    mockMvc
        .perform(get("/api/v2/home/posts/latest"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "최신 게시글 조회에 성공했습니다.",
                      "data": {"recentPosts": [{
                        "id": 11, "userId": 7, "profileImage": "https://img/p.png", "name": "홍길동",
                        "generationAndPart": "38기 서버", "category": "자유", "title": "제목",
                        "content": "내용", "webLink": "https://pg/11", "isOutdated": false
                      }]}
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 인기_게시글_응답_키() throws Exception {
    given(homeFacade.getPlaygroundPopularPosts())
        .willReturn(
            List.of(
                new PlaygroundPopularPost(
                    21L, 8L, null, "김솝트", "38기 웹", 1, "질문", "인기글", "본문", "https://pg/21")));

    mockMvc
        .perform(get("/api/v2/home/posts/popular"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "인기 게시글 조회에 성공했습니다.",
                      "data": {"popularPosts": [{
                        "id": 21, "userId": 8, "profileImage": null, "name": "김솝트", "generationAndPart": "38기 웹", "rank": 1,
                        "category": "질문", "title": "인기글", "content": "본문", "webLink": "https://pg/21"
                      }]}
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 플로팅_버튼_응답_키() throws Exception {
    given(homeFacade.getFloatingButtonInfo(USER_ID))
        .willReturn(
            new FloatingButton("https://img/f.png", "제목", "펼침", "접힘", "버튼", "https://link", true));

    mockMvc
        .perform(get("/api/v2/home/floating-button"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "플로팅 버튼 조회에 성공했습니다.",
                      "data": {
                        "imageUrl": "https://img/f.png", "title": "제목", "expandedSubTitle": "펼침",
                        "collapsedSubtitle": "접힘", "actionButtonName": "버튼", "linkUrl": "https://link",
                        "isActive": true
                      }
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 후기_폼_서비스_행_없음_404_봉투() throws Exception {
    given(homeFacade.getReviewFormInfo(USER_ID))
        .willThrow(new HomeException(HomeFailure.NOT_FOUND_APP_SERVICE));

    mockMvc
        .perform(get("/api/v2/home/review-form"))
        .andExpect(status().isNotFound())
        .andExpect(
            content()
                .json(
                    """
                    {"success": false, "message": "앱 서비스 정보를 찾을 수 없습니다.", "data": null}
                    """,
                    JsonCompareMode.STRICT));
  }
}
