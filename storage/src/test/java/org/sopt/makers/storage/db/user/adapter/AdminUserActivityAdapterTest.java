package org.sopt.makers.storage.db.user.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.domain.user.Team;
import org.sopt.makers.storage.db.user.entity.UserActivityHistoryEntity;
import org.sopt.makers.storage.db.user.entity.UserEntity;
import org.sopt.makers.storage.support.AdminJpaTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@ContextConfiguration(classes = AdminJpaTestConfig.class)
@Import(AdminUserActivityAdapter.class)
class AdminUserActivityAdapterTest {
  @Autowired private TestEntityManager em;
  @Autowired private AdminUserActivityAdapter adapter;

  private UserEntity user(String name, String phone) {
    return em.persist(
        UserEntity.create(name, phone, null, null, null, phone, OAuthPlatform.GOOGLE));
  }

  private void activity(UserEntity user, int generation, Part part, boolean isSopt, Float score) {
    em.persist(
        UserActivityHistoryEntity.create(
            user, generation, isSopt ? null : Team.MAKERS, part, Role.MEMBER, isSopt, score));
  }

  @Test
  @DisplayName("다른 기수와 임원진 활동을 제외하고 지정한 기수의 회원을 구성한다")
  void selectsSpecifiedSoptActivity() {
    var user = user("홍길동", "01000000001");
    activity(user, 38, Part.WEB, true, 1.0f);
    activity(user, 39, Part.SERVER, true, 1.5f);
    activity(user, 40, Part.DESIGN, false, 0.0f);
    em.flush();
    em.clear();
    var member = adapter.findMemberByUserIdAndGeneration(user.getId(), 38).orElseThrow();
    assertThat(member.id()).isEqualTo(user.getId());
    assertThat(member.name()).isEqualTo("홍길동");
    assertThat(member.generation()).isEqualTo(38);
    assertThat(member.part()).isEqualTo(Part.WEB);
    assertThat(member.attendanceScore()).isEqualTo(1.0f);
  }

  @Test
  @DisplayName("다른 기수 활동만 있거나 임원진 활동만 있으면 회원 조회 결과가 없다")
  void excludesUserWithoutSoptActivity() {
    var user = user("홍길동", "01000000001");
    activity(user, 40, Part.SERVER, false, null);
    activity(user, 38, Part.WEB, true, null);
    em.flush();
    em.clear();
    assertThat(adapter.findMemberByUserIdAndGeneration(user.getId(), 40)).isEmpty();
  }

  @Test
  @DisplayName("기수와 파트로 필터링하고 회원 이름순으로 페이지를 조회한다")
  void filtersAndOrdersMembers() {
    var hong = user("홍길동", "01000000001");
    var kim = user("김솝트", "01000000002");
    var park = user("박솝트", "01000000003");
    activity(hong, 39, Part.SERVER, true, null);
    activity(kim, 39, Part.SERVER, true, 1.5f);
    activity(park, 39, Part.WEB, true, null);
    activity(kim, 39, Part.SERVER, false, null);
    activity(hong, 38, Part.SERVER, true, null);
    em.flush();
    em.clear();
    assertThat(adapter.countByGenerationAndPart(39, Part.SERVER)).isEqualTo(2);
    assertThat(adapter.findByGenerationAndPart(39, Part.SERVER, 0, 1))
        .extracting(m -> m.name())
        .containsExactly("김솝트");
    assertThat(adapter.findByGenerationAndPart(39, Part.SERVER, 1, 1))
        .singleElement()
        .satisfies(m -> assertThat(m.attendanceScore()).isEqualTo(2.0f));
    assertThat(adapter.countByGenerationAndPart(39, Part.ALL)).isEqualTo(3);
  }

  @Test
  @DisplayName("회원 점수 변경은 같은 기수의 SOPT 활동에만 반영한다")
  void updatesOnlySoptScore() {
    var user = user("홍길동", "01000000001");
    activity(user, 39, Part.SERVER, true, 2.0f);
    activity(user, 39, Part.SERVER, false, 3.0f);
    activity(user, 38, Part.SERVER, true, 1.0f);
    em.flush();
    em.clear();
    adapter.updateAttendanceScore(user.getId(), 39, 1.5f);
    var activities =
        em.getEntityManager()
            .createQuery(
                "select a from UserActivityHistoryEntity a where a.user.id = :id",
                UserActivityHistoryEntity.class)
            .setParameter("id", user.getId())
            .getResultList();
    assertThat(activities)
        .filteredOn(a -> a.getGeneration() == 39 && a.isSopt())
        .extracting(UserActivityHistoryEntity::getAttendanceScore)
        .containsExactly(1.5f);
    assertThat(activities)
        .filteredOn(a -> !a.isSopt())
        .extracting(UserActivityHistoryEntity::getAttendanceScore)
        .containsExactly(3.0f);
    assertThat(activities)
        .filteredOn(a -> a.getGeneration() == 38)
        .extracting(UserActivityHistoryEntity::getAttendanceScore)
        .containsExactly(1.0f);
  }
}
