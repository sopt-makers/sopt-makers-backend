package org.sopt.makers.domain.app.soptletter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sopt.makers.domain.app.soptletter.exception.SoptLetterFailure.NICKNAME_IS_FULL;
import static org.sopt.makers.domain.app.soptletter.exception.SoptLetterFailure.NOT_FOUND_SOPT_LETTER_PROFILE;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.soptletter.SoptLetterProfile;
import org.sopt.makers.domain.app.soptletter.exception.SoptLetterException;
import org.sopt.makers.domain.app.soptletter.port.AnonymousNicknameGeneratorPort;
import org.sopt.makers.domain.app.soptletter.support.InMemorySoptLetterProfileRepositoryPort;

@DisplayName("SoptLetterProfileService 테스트")
class SoptLetterProfileServiceTest {

  private final InMemorySoptLetterProfileRepositoryPort profilePort =
      new InMemorySoptLetterProfileRepositoryPort();

  @Test
  @DisplayName("프로필이 없으면 익명 닉네임으로 새로 만든다")
  void createsProfileWhenMissing() {
    SoptLetterProfileService service = service(List.of("익명의 매운 떡볶이"));

    SoptLetterProfile profile = service.getOrCreate(1L);

    assertThat(profile.nickname()).isEqualTo("익명의 매운 떡볶이");
    assertThat(profile.isOnboarded()).isFalse();
  }

  @Test
  @DisplayName("이미 있는 프로필은 새로 만들지 않는다")
  void reusesExistingProfile() {
    SoptLetterProfileService service = service(List.of("익명의 매운 떡볶이"), List.of("익명의 시원한 냉면"));
    SoptLetterProfile first = service.getOrCreate(1L);

    SoptLetterProfile second = service.getOrCreate(1L);

    assertThat(second.id()).isEqualTo(first.id());
    assertThat(second.nickname()).isEqualTo(first.nickname());
  }

  @Test
  @DisplayName("후보 닉네임이 이미 쓰이고 있으면 다음 후보로 넘어간다")
  void retriesWhenNicknameTaken() {
    SoptLetterProfileService service = service(List.of("익명의 매운 떡볶이"), List.of("익명의 시원한 냉면"));
    service.getOrCreate(1L);

    SoptLetterProfile other = service.getOrCreate(2L);

    assertThat(other.nickname()).isEqualTo("익명의 시원한 냉면");
  }

  @Test
  @DisplayName("계속 겹치면 닉네임 포화로 실패한다")
  void failsWhenAllCandidatesTaken() {
    SoptLetterProfileService service =
        service(
            List.of("익명의 매운 떡볶이"),
            List.of("익명의 매운 떡볶이"),
            List.of("익명의 매운 떡볶이"),
            List.of("익명의 매운 떡볶이"));
    service.getOrCreate(1L);

    assertThatThrownBy(() -> service.getOrCreate(2L))
        .isInstanceOf(SoptLetterException.class)
        .extracting("error")
        .isEqualTo(NICKNAME_IS_FULL);
  }

  @Test
  @DisplayName("온보딩을 완료하면 완료 상태로 바뀐다")
  void completesOnboarding() {
    SoptLetterProfileService service = service(List.of("익명의 매운 떡볶이"));
    service.getOrCreate(1L);

    assertThat(service.completeOnboarding(1L).isOnboarded()).isTrue();
    assertThat(service.getByUserId(1L).isOnboarded()).isTrue();
  }

  @Test
  @DisplayName("프로필이 없는 유저를 조회하면 예외가 발생한다")
  void throwsWhenProfileMissing() {
    assertThatThrownBy(() -> service(List.of("익명의 매운 떡볶이")).getByUserId(999L))
        .isInstanceOf(SoptLetterException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_SOPT_LETTER_PROFILE);
  }

  @SafeVarargs
  private SoptLetterProfileService service(List<String>... candidateBatches) {
    Deque<List<String>> batches = new ArrayDeque<>(List.of(candidateBatches));
    AnonymousNicknameGeneratorPort generator =
        count -> batches.isEmpty() ? List.of("익명의 매운 떡볶이") : batches.poll();
    return new SoptLetterProfileService(profilePort, generator);
  }
}
