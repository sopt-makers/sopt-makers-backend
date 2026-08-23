package org.sopt.makers.domain.playground.project.port;

import java.util.List;

public interface ProjectUserPort {

    List<ProjectUserInfo> getProjectUserInfosByIds(List<Long> userIds);

    record ProjectUserInfo(
            Long id,
            String name,
            String profileImage,
            List<Integer> generations,
            boolean hasProfile
    ) {}
}
