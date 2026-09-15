package org.sopt.makers.domain.playground.community.member.port;

import java.util.List;

/** 커뮤니티 작성자 표시 정보를 조회하기 위한 Port. 실제 구현은 domain-user가 노출한 목적별 Port를 사용한다. */
public interface CommunityMemberPort {

  List<MemberInfo> findMemberInfosByIds(List<Long> memberIds);

  record MemberInfo(
      Long id,
      String name,
      String profileImage,
      List<ActivityInfo> activities,
      CareerInfo lastCareer) {}

  record ActivityInfo(int generation, String part, String team, boolean isSopt) {}

  record CareerInfo(String companyName, String title) {}
}
