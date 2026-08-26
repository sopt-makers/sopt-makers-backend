package org.sopt.makers.domain.app.poke;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sopt.makers.domain.app.poke.exception.PokeFailure.NOT_FOUND_FRIENDSHIP;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.poke.exception.PokeException;

@DisplayName("찌르기 도메인 모델 테스트")
class PokeDomainModelTest {

  private PokedUserInfo pokedUserInfo(int pokeNum, String anonymousName, List<String> mutuals) {
    return new PokedUserInfo(
        2L,
        "김솝트",
        "profile.png",
        34L,
        "서버",
        new Relationship(pokeNum, Friendship.decideRelationName(pokeNum), anonymousName),
        mutuals);
  }

  @Test
  @DisplayName("함께 아는 친구가 없으면 새로운 친구로 안내한다")
  void mutualRelationMessageWithNoMutualFriend() {
    assertThat(pokedUserInfo(1, "", List.of()).getMutualRelationMessage()).isEqualTo("새로운 친구");
  }

  @Test
  @DisplayName("함께 아는 친구가 한 명이면 그 이름의 친구로 안내한다")
  void mutualRelationMessageWithOneMutualFriend() {
    assertThat(pokedUserInfo(1, "", List.of("이앤플")).getMutualRelationMessage())
        .isEqualTo("이앤플의 친구");
  }

  @Test
  @DisplayName("함께 아는 친구가 여러 명이면 첫 친구 외 나머지 수를 붙인다")
  void mutualRelationMessageWithManyMutualFriends() {
    assertThat(pokedUserInfo(1, "", List.of("이앤플", "김솝트", "박메이커")).getMutualRelationMessage())
        .isEqualTo("이앤플 외 2명과 친구");
  }

  @Test
  @DisplayName("찌른 횟수가 2회 미만이면 첫 만남이다")
  void isFirstMeet() {
    assertThat(pokedUserInfo(0, "", List.of()).isFirstMeet()).isTrue();
    assertThat(pokedUserInfo(1, "", List.of()).isFirstMeet()).isTrue();
    assertThat(pokedUserInfo(2, "", List.of()).isFirstMeet()).isFalse();
  }

  @Test
  @DisplayName("프로필 이미지와 메시지가 null 이면 빈 문자열로 내려간다")
  void nullProfileImageAndMessageBecomeEmpty() {
    PokedUserInfo info =
        new PokedUserInfo(
            2L, "김솝트", null, 34L, "서버", new Relationship(3, "친한친구", "익명의 김밥"), List.of());

    SimplePokeProfileData profile = SimplePokeProfileData.of(info, null, false, false, true);

    assertThat(profile.profileImage()).isEmpty();
    assertThat(profile.message()).isEmpty();
    assertThat(profile.anonymousName()).isEqualTo("익명의 김밥");
  }

  @Test
  @DisplayName("익명 찌르기가 아니면 익명 이름을 내려보내지 않는다")
  void anonymousNameHiddenWhenNotAnonymous() {
    SimplePokeProfileData profile =
        SimplePokeProfileData.of(pokedUserInfo(3, "익명의 김밥", List.of()), "안녕", true, true, false);

    assertThat(profile.anonymousName()).isEmpty();
    assertThat(profile.isAnonymous()).isFalse();
    assertThat(profile.message()).isEqualTo("안녕");
    assertThat(profile.pokeNum()).isEqualTo(3);
    assertThat(profile.relationName()).isEqualTo("친한친구");
    assertThat(profile.mutualRelationMessage()).isEqualTo("새로운 친구");
    assertThat(profile.isFirstMeet()).isTrue();
    assertThat(profile.isAlreadyPoke()).isTrue();
  }

  @Test
  @DisplayName("친구 관계는 타입 문자열로 찾는다")
  void getFriendshipByValue() {
    assertThat(Friendship.getFriendshipByValue("nonfriend")).isEqualTo(Friendship.NON_FRIEND);
    assertThat(Friendship.getFriendshipByValue("new")).isEqualTo(Friendship.NEW_FRIEND);
    assertThat(Friendship.getFriendshipByValue("bestfriend")).isEqualTo(Friendship.BEST_FRIEND);
    assertThat(Friendship.getFriendshipByValue("soulmate")).isEqualTo(Friendship.SOULMATE);
  }

  @Test
  @DisplayName("없는 타입 문자열이면 NOT_FOUND_FRIENDSHIP 예외가 발생한다")
  void getFriendshipByValueThrowsWhenUnknown() {
    assertThatThrownBy(() -> Friendship.getFriendshipByValue("bestFriend"))
        .isInstanceOf(PokeException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_FRIENDSHIP);
  }

  @Test
  @DisplayName("친밀도 이름은 2, 5, 11 회를 경계로 바뀐다")
  void decideRelationName() {
    assertThat(Friendship.decideRelationName(1)).isEqualTo("새로운 친구");
    assertThat(Friendship.decideRelationName(2)).isEqualTo("친한친구");
    assertThat(Friendship.decideRelationName(4)).isEqualTo("친한친구");
    assertThat(Friendship.decideRelationName(5)).isEqualTo("단짝친구");
    assertThat(Friendship.decideRelationName(10)).isEqualTo("단짝친구");
    assertThat(Friendship.decideRelationName(11)).isEqualTo("천생연분");
  }
}
