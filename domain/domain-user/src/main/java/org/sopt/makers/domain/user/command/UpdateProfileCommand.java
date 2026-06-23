package org.sopt.makers.domain.user.command;

import java.util.List;
import org.sopt.makers.domain.user.UserCareer;
import org.sopt.makers.domain.user.UserFavor;
import org.sopt.makers.domain.user.UserLink;
import org.sopt.makers.domain.user.WorkPreference;

public record UpdateProfileCommand(
    String email,
    String phone,
    String profileImage,
    List<ActivityUpdateCommand> activityUpdates,
    String address,
    String university,
    String major,
    String introduction,
    String skill,
    String mbti,
    String mbtiDescription,
    Double sojuCapacity,
    String interest,
    UserFavor userFavor,
    String idealType,
    String selfIntroduction,
    Boolean allowOfficial,
    Boolean isPhoneBlind,
    WorkPreference workPreference,
    List<UserLink> links,
    List<UserCareer> careers) {}
