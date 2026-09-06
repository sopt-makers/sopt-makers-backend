package org.sopt.makers.domain.playground.member.profile;

import org.sopt.makers.domain.playground.member.ask.AskPreview;
import org.sopt.makers.domain.user.User;

public record UserProfileListItem(User user, boolean isCoffeeChatActivate, AskPreview questionPreview) {}
