package org.sopt.makers.domain.playground.member.profile;

import java.util.List;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.UserCareer;

public record MakersUserProfile(
    Long id,
    String name,
    String profileImage,
    List<Activity> activities,
    List<UserCareer> careers) {}
