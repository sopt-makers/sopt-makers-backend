package org.sopt.makers.api.controller.app.poke;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.controller.app.AppChannelMockMvc;
import org.sopt.makers.domain.app.poke.EachRelationFriendListData;
import org.sopt.makers.domain.app.poke.FriendRecommendType;
import org.sopt.makers.domain.app.poke.Friendship;
import org.sopt.makers.domain.app.poke.PokeMessage;
import org.sopt.makers.domain.app.poke.PokeMessageType;
import org.sopt.makers.domain.app.poke.PokeToMeHistoryData;
import org.sopt.makers.domain.app.poke.RecommendedFriends;
import org.sopt.makers.domain.app.poke.RecommendedFriendsByType;
import org.sopt.makers.domain.app.poke.SimplePokeProfileData;
import org.sopt.makers.domain.app.poke.exception.PokeException;
import org.sopt.makers.domain.app.poke.exception.PokeFailure;
import org.sopt.makers.domain.app.poke.facade.PokeFacade;
import org.sopt.makers.domain.user.PokeUserProfile;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

class PokeControllerTest {

  private static final Long USER_ID = 1L;

  private static final String FRIEND_PROFILE_JSON =
      """
      {
        "userId": 2,
        "profileImage": "https://img.example/profile.png",
        "name": "차은우",
        "message": "안녕",
        "generation": 35,
        "part": "서버",
        "pokeNum": 3,
        "relationName": "친한친구",
        "mutualRelationMessage": "서로 3번 찔렀어요",
        "isFirstMeet": false,
        "isAlreadyPoke": true,
        "isAnonymous": true,
        "anonymousName": "익명의 사자"
      }""";

  private final PokeFacade pokeFacade = mock(PokeFacade.class);

  private final MockMvc mockMvc = AppChannelMockMvc.of(new PokeController(pokeFacade), USER_ID);

  private static SimplePokeProfileData friendProfile() {
    return new SimplePokeProfileData(
        2L,
        "https://img.example/profile.png",
        "차은우",
        "안녕",
        35L,
        "서버",
        3,
        "친한친구",
        "서로 3번 찔렀어요",
        false,
        true,
        true,
        "익명의 사자");
  }

  @Test
  void 신규_유저_여부_성공_응답_모양() throws Exception {
    given(pokeFacade.getIsNewUser(USER_ID)).willReturn(true);

    mockMvc
        .perform(get("/api/v2/poke/new"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {"success": true, "message": "신규 유저 여부 조회에 성공했습니다.", "data": {"isNew": true}}
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 찌르기_메시지_조회_성공_응답_모양() throws Exception {
    given(pokeFacade.getPokingMessageHeader("pokeAll")).willReturn("함께 보낼 메시지를 골라주세요");
    given(pokeFacade.getPokingMessages("pokeAll"))
        .willReturn(
            List.of(
                new PokeMessage(1L, "친해지고 싶어요", PokeMessageType.POKE_ALL),
                new PokeMessage(99L, "콕", PokeMessageType.POKE_ALL)));

    mockMvc
        .perform(get("/api/v2/poke/message").param("messageType", "pokeAll"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "찌르기 메시지 조회에 성공했습니다.",
                      "data": {
                        "header": "함께 보낼 메시지를 골라주세요",
                        "messages": [
                          {"messageId": 1, "content": "친해지고 싶어요"},
                          {"messageId": 99, "content": "콕"}
                        ]
                      }
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 찌르기_성공_응답_모양() throws Exception {
    given(pokeFacade.pokeFriend(USER_ID, 2L, "안녕", true)).willReturn(10L);
    given(pokeFacade.getPokeHistoryProfile(USER_ID, 2L, 10L)).willReturn(friendProfile());

    mockMvc
        .perform(
            put("/api/v2/poke/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"message": "안녕", "isAnonymous": true}
                    """))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {"success": true, "message": "찌르기에 성공했습니다.", "data": %s}
                    """
                        .formatted(FRIEND_PROFILE_JSON),
                    JsonCompareMode.STRICT));
  }

  @Test
  void 찌르기_isAnonymous_생략이면_false_로_전달() throws Exception {
    given(pokeFacade.pokeFriend(USER_ID, 2L, "안녕", false)).willReturn(10L);
    given(pokeFacade.getPokeHistoryProfile(USER_ID, 2L, 10L)).willReturn(friendProfile());

    mockMvc
        .perform(
            put("/api/v2/poke/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"message": "안녕"}
                    """))
        .andExpect(status().isOk());

    then(pokeFacade).should().pokeFriend(USER_ID, 2L, "안녕", false);
  }

  @Test
  void 나를_찌른_친구가_없으면_data_null() throws Exception {
    given(pokeFacade.getRandomUnRepliedPokeMeHistory(USER_ID)).willReturn(null);

    mockMvc
        .perform(get("/api/v2/poke/to/me"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {"success": true, "message": "나를 찌른 친구 단일 조회에 성공했습니다.", "data": null}
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 나를_찌른_친구_목록_성공_응답_모양() throws Exception {
    given(pokeFacade.getAllPokeMeHistory(eq(USER_ID), any(Pageable.class)))
        .willReturn(new PokeToMeHistoryData(List.of(friendProfile()), 0, 25, 0));

    mockMvc
        .perform(get("/api/v2/poke/to/me/list"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "나를 찌른 친구 목록 조회에 성공했습니다.",
                      "data": {"history": [%s], "totalPageSize": 0, "pageSize": 25, "pageNum": 0}
                    }
                    """
                        .formatted(FRIEND_PROFILE_JSON),
                    JsonCompareMode.STRICT));
  }

  @Test
  void 친구_목록_전체_카테고리_성공_응답_모양() throws Exception {
    given(pokeFacade.getTwoFriendByFriendship(USER_ID, Friendship.NEW_FRIEND))
        .willReturn(List.of(friendProfile()));
    given(pokeFacade.getFriendSizeByFriendship(USER_ID, Friendship.NEW_FRIEND)).willReturn(1);
    given(pokeFacade.getTwoFriendByFriendship(USER_ID, Friendship.BEST_FRIEND))
        .willReturn(List.of());
    given(pokeFacade.getFriendSizeByFriendship(USER_ID, Friendship.BEST_FRIEND)).willReturn(2);
    given(pokeFacade.getTwoFriendByFriendship(USER_ID, Friendship.SOULMATE)).willReturn(List.of());
    given(pokeFacade.getFriendSizeByFriendship(USER_ID, Friendship.SOULMATE)).willReturn(3);

    mockMvc
        .perform(get("/api/v2/poke/friend/list"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "친구 목록 조회에 성공했습니다.",
                      "data": {
                        "newFriend": [%s],
                        "newFriendSize": 1,
                        "bestFriend": [],
                        "bestFriendSize": 2,
                        "soulmate": [],
                        "soulmateSize": 3,
                        "totalSize": 6
                      }
                    }
                    """
                        .formatted(FRIEND_PROFILE_JSON),
                    JsonCompareMode.STRICT));
  }

  @Test
  void 친구_목록_타입별_성공_응답_모양() throws Exception {
    given(
            pokeFacade.getAllFriendByFriendship(
                eq(USER_ID), eq(Friendship.BEST_FRIEND), any(Pageable.class)))
        .willReturn(new EachRelationFriendListData(List.of(friendProfile()), 1, 1, 25, 0));

    mockMvc
        .perform(get("/api/v2/poke/friend/list").param("type", "bestfriend"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "친구 목록 조회에 성공했습니다.",
                      "data": {
                        "friendList": [%s],
                        "totalSize": 1,
                        "totalPageSize": 1,
                        "pageSize": 25,
                        "pageNum": 0
                      }
                    }
                    """
                        .formatted(FRIEND_PROFILE_JSON),
                    JsonCompareMode.STRICT));
  }

  @Test
  void 친구_목록_없는_타입_404_BaseResponse_포맷() throws Exception {
    mockMvc
        .perform(get("/api/v2/poke/friend/list").param("type", "없는값"))
        .andExpect(status().isNotFound())
        .andExpect(
            content()
                .json(
                    """
                    {"success": false, "message": "해당 친구관계는 존재하지 않습니다.", "data": null}
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 친구_추천_성공_응답_모양_비친구_기본값() throws Exception {
    given(pokeFacade.getRecommendedFriendsByTypeList(null, 2, USER_ID))
        .willReturn(
            new RecommendedFriends(
                List.of(
                    new RecommendedFriendsByType(
                        FriendRecommendType.GENERATION,
                        "나와 같은 기수예요",
                        List.of(new PokeUserProfile(3L, "홍길동", null, 36L, "iOS"))))));

    mockMvc
        .perform(get("/api/v2/poke/random").param("size", "2"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "친구 추천 조회에 성공했습니다.",
                      "data": {
                        "randomInfoList": [
                          {
                            "randomType": "GENERATION",
                            "randomTitle": "나와 같은 기수예요",
                            "userInfoList": [
                              {
                                "userId": 3,
                                "profileImage": "",
                                "name": "홍길동",
                                "message": "",
                                "generation": 36,
                                "part": "iOS",
                                "pokeNum": 0,
                                "relationName": "",
                                "mutualRelationMessage": "",
                                "isFirstMeet": true,
                                "isAlreadyPoke": false,
                                "isAnonymous": false,
                                "anonymousName": ""
                              }
                            ]
                          }
                        ]
                      }
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 중복_찌르기_409_BaseResponse_포맷() throws Exception {
    given(pokeFacade.pokeFriend(USER_ID, 2L, "안녕", true))
        .willThrow(new PokeException(PokeFailure.DUPLICATE_POKE));

    mockMvc
        .perform(
            put("/api/v2/poke/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"message": "안녕", "isAnonymous": true}
                    """))
        .andExpect(status().isConflict())
        .andExpect(
            content()
                .json(
                    """
                    {"success": false, "message": "이미 찌르기를 보낸 친구입니다.", "data": null}
                    """,
                    JsonCompareMode.STRICT));
  }
}
