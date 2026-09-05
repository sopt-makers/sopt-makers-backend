package org.sopt.makers.api.controller.app.soptamp;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.controller.app.AppChannelMockMvc;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.facade.SoptampFacade;
import org.sopt.makers.domain.app.soptamp.facade.SoptampFacade.UserMissions;
import org.sopt.makers.domain.app.soptamp.mission.Mission;
import org.sopt.makers.domain.app.soptamp.rank.PartRank;
import org.sopt.makers.domain.app.soptamp.rank.UserRank;
import org.sopt.makers.domain.app.soptamp.rank.service.RankService;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

class SoptampRankControllerTest {

  private static final Long USER_ID = 1L;

  private final RankService rankService = mock(RankService.class);
  private final SoptampFacade soptampFacade = mock(SoptampFacade.class);

  private final MockMvc mockMvc =
      AppChannelMockMvc.of(new SoptampRankController(rankService, soptampFacade), USER_ID);

  @Test
  void 현재_기수_랭킹_목록_응답_모양() throws Exception {
    given(rankService.findCurrentRanks())
        .willReturn(List.of(new UserRank(1, "김앱짱", 15L, "1등이 되고 말거야!")));

    mockMvc
        .perform(get("/api/v2/rank/current"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "현재 기수 랭킹 목록 조회에 성공했습니다.",
                      "data": [
                        {
                          "rank": 1,
                          "nickname": "김앱짱",
                          "point": 15,
                          "profileMessage": "1등이 되고 말거야!"
                        }
                      ]
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 파트별_현재_기수_랭킹_목록_응답_모양() throws Exception {
    given(rankService.findCurrentRanksByPart(Part.SERVER))
        .willReturn(List.of(new UserRank(1, "서버이지훈", 30L, "뒹굴뒹굴 ~,~")));

    mockMvc
        .perform(get("/api/v2/rank/current/part/SERVER"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "파트별 현재 기수 랭킹 목록 조회에 성공했습니다.",
                      "data": [
                        {
                          "rank": 1,
                          "nickname": "서버이지훈",
                          "point": 30,
                          "profileMessage": "뒹굴뒹굴 ~,~"
                        }
                      ]
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 알_수_없는_파트는_400_BaseResponse_포맷() throws Exception {
    mockMvc
        .perform(get("/api/v2/rank/current/part/NOT_A_PART"))
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .json(
                    """
                    {"success": false, "message": "입력한 값의 타입이 잘못되었습니다", "data": null}
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 파트끼리의_랭킹_목록_응답_모양() throws Exception {
    given(rankService.findAllPartRanks())
        .willReturn(
            List.of(
                new PartRank("기획", 1, 30L, new BigDecimal("30.00")),
                new PartRank("디자인", 2, 2L, new BigDecimal("2.50"))));

    mockMvc
        .perform(get("/api/v2/rank/part"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "파트끼리의 랭킹 목록 조회에 성공했습니다.",
                      "data": [
                        {"part": "기획", "rank": 1, "points": 30, "pointsDecimal": 30.00},
                        {"part": "디자인", "rank": 2, "points": 2, "pointsDecimal": 2.50}
                      ]
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 유저_미션_상세_응답_모양() throws Exception {
    given(soptampFacade.findUserMissionsByNickname("김앱짱"))
        .willReturn(
            new UserMissions(
                new SoptampUser(10L, 1L, "1등이 되고 말거야!", 15L, "김앱짱", 38L, SoptampPart.PLAN),
                List.of(new Mission(1L, "팀원 칭찬하기", 1, true, List.of("https://img/1.png")))));

    mockMvc
        .perform(get("/api/v2/rank/detail").param("nickname", "김앱짱"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "유저 미션 정보 상세 조회에 성공했습니다.",
                      "data": {
                        "nickname": "김앱짱",
                        "profileMessage": "1등이 되고 말거야!",
                        "userMissions": [
                          {
                            "id": 1,
                            "title": "팀원 칭찬하기",
                            "level": 1,
                            "display": true,
                            "profileImage": ["https://img/1.png"]
                          }
                        ]
                      }
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 미션_이미지가_비어_있으면_profileImage는_빈_배열로_나간다() throws Exception {
    given(soptampFacade.findUserMissionsByNickname("김앱짱"))
        .willReturn(
            new UserMissions(
                new SoptampUser(10L, 1L, "1등이 되고 말거야!", 15L, "김앱짱", 38L, SoptampPart.PLAN),
                List.of(new Mission(1L, "팀원 칭찬하기", 1, true, null))));

    mockMvc
        .perform(get("/api/v2/rank/detail").param("nickname", "김앱짱"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "유저 미션 정보 상세 조회에 성공했습니다.",
                      "data": {
                        "nickname": "김앱짱",
                        "profileMessage": "1등이 되고 말거야!",
                        "userMissions": [
                          {
                            "id": 1,
                            "title": "팀원 칭찬하기",
                            "level": 1,
                            "display": true,
                            "profileImage": []
                          }
                        ]
                      }
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 닉네임에_해당하는_유저가_없으면_404() throws Exception {
    given(soptampFacade.findUserMissionsByNickname("없는닉"))
        .willThrow(new SoptampException(SoptampFailure.NOT_FOUND_SOPTAMP_USER));

    mockMvc
        .perform(get("/api/v2/rank/detail").param("nickname", "없는닉"))
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
  void 앱잼_시즌이면_랭킹_조회는_400() throws Exception {
    given(rankService.findCurrentRanks())
        .willThrow(new SoptampException(SoptampFailure.INVALID_APPJAM_SEASON_REQUEST));

    mockMvc
        .perform(get("/api/v2/rank/current"))
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .json(
                    """
                    {"success": false, "message": "앱잼탬프 시즌이므로 부적절한 요청입니다.", "data": null}
                    """,
                    JsonCompareMode.STRICT));
  }
}
