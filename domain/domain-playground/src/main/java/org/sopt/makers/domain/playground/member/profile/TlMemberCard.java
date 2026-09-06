package org.sopt.makers.domain.playground.member.profile;

import org.sopt.makers.domain.playground.member.tl.TlUser;
import org.sopt.makers.domain.user.User;

public record TlMemberCard(User user, TlUser tlUser) {}
