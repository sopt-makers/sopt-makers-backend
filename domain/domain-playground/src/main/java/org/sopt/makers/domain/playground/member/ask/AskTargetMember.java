package org.sopt.makers.domain.playground.member.ask;

import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.UserCareer;

/** 질문 가능 대상 멤버 카드. latestActivity/career는 없을 수 있다. */
public record AskTargetMember(User user, Activity latestActivity, UserCareer career) {}
