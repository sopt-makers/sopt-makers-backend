package org.sopt.makers.domain.user.command;

import org.sopt.makers.domain.user.Team;

public record ActivityUpdateCommand(Long activityId, Team team) {}
