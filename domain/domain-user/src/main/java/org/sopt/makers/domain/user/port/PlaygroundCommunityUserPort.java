package org.sopt.makers.domain.user.port;

import java.util.List;

/** 커뮤니티 도메인이 게시글 작성자 표시(이름/프로필/활동/커리어)를 조립하기 위해 사용하는 목적별 Port. */
public interface PlaygroundCommunityUserPort {

  List<CommunityMemberInfo> getCommunityMemberInfosByIds(List<Long> userIds);

  record CommunityMemberInfo(
      Long id,
      String name,
      String profileImage,
      List<ActivityInfo> activities,
      CareerInfo lastCareer) {}

  record ActivityInfo(int generation, String part, String team, boolean isSopt) {}

  record CareerInfo(String companyName, String title) {}
}
