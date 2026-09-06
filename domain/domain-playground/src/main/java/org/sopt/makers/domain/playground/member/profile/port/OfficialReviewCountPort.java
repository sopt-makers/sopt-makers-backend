package org.sopt.makers.domain.playground.member.profile.port;

/** 공식 홈페이지(도메인-official)에 등록된 스타트업/솝티클 후기 수를 작성자 이름 기준으로 조회한다. */
public interface OfficialReviewCountPort {

  int countReviewsByAuthor(String author);
}
