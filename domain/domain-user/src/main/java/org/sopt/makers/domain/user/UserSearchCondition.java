package org.sopt.makers.domain.user;

import org.sopt.makers.core.type.Part;

public record UserSearchCondition(
    Integer generation, Part part, String name, Team team, Boolean isAdmin) {}
