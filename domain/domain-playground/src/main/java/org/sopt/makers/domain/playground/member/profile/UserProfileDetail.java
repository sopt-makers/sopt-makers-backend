package org.sopt.makers.domain.playground.member.profile;

import java.util.List;
import org.sopt.makers.domain.playground.project.Project;
import org.sopt.makers.domain.user.User;

public record UserProfileDetail(
    User user,
    boolean isMine,
    boolean isCoffeeChatActivate,
    boolean hasRecentQuestion,
    List<Project> projects) {}
